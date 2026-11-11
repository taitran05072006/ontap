# Ansible provisioning and two-node deployment

This directory provisions two Ubuntu nodes:

- common packages, timezone and unattended security upgrades
- Docker Engine and the Docker Compose plugin
- UFW with SSH, HTTP/HTTPS and restricted private application traffic
- Tailscale client installation and non-interactive login when a Vault auth key is configured
- Nginx reverse proxy and load balancer on Ubuntu 1
- backend and frontend on Ubuntu 1, backend on Ubuntu 2
- one MySQL instance on Ubuntu 1, shared by both backends

The default inventory is:

| Node | Address | Services |
| --- | --- | --- |
| Ubuntu 1 | `192.168.64.4` | Nginx, backend 1, frontend, MySQL |
| Ubuntu 2 | `192.168.64.7` | backend 2 |

The database is intentionally a single instance. This is suitable for a lab or
small deployment, but Ubuntu 1 remains a failure point for both the load
balancer and database. Use a managed database or a third host before treating
this as highly available production.

## Requirements

Install Ansible on the control machine and ensure the VM is reachable over SSH:

```bash
ansible --version
ansible-galaxy collection install -r requirements.yml
ssh taitran@192.168.64.4
```

The VM currently uses user `taitran` and the key `~/.ssh/ubuntunew`. Change
`ansible_user` and `ansible_ssh_private_key_file` in `inventory/hosts.yml` if
your VM uses a different user or key. Because this key has a passphrase, load
it into the SSH agent before running Ansible:

```bash
eval "$(ssh-agent -s)"
ssh-add --apple-use-keychain ~/.ssh/ubuntunew
```

If Ansible later reports `Permission denied (publickey,password)`, reload the
key in the current terminal and test SSH before retrying:

```bash
eval "$(ssh-agent -s)"
ssh-add --apple-use-keychain ~/.ssh/ubuntunew
ssh-add -l
ssh -o BatchMode=yes -i ~/.ssh/ubuntunew taitran@192.168.64.4 true
ansible all -m ping -e ansible_become=false
```

The direct SSH command must succeed before running a playbook.

## Connect the VM to Tailscale

The `tailscale` role installs and starts `tailscaled`. To have Ansible also
log the VM in, create a **dedicated auth key for this VM** in the Tailscale
admin console, then store it in Ansible Vault. For a long-lived VM, use an
auth key with the `tag:server` tag (and ensure your tailnet permits that tag).
Do not reuse the GitHub Actions OAuth credentials or commit the auth key.

From the `ansible` directory on the Mac, create the encrypted variables file:

```bash
ansible-vault create inventory/group_vars/all/vault.yml
```

In the editor that opens, enter:

```yaml
tailscale_auth_key: "PASTE_THE_TAILSCALE_AUTH_KEY_HERE"
```

Save and close the editor. The file is ignored by Git and encrypted by
Ansible Vault. Then provision the VM:

```bash
ansible-playbook playbooks/site.yml --ask-become-pass --ask-vault-pass
```

The playbook uses the VM's current SSH address from `inventory/hosts.yml` to
connect, then Tailscale adds it to your tailnet. If the VM is already logged
in, rerunning the playbook does not log it in again. Check the new address on
the VM with `tailscale ip -4`; use that Tailscale IP (or the VM's MagicDNS
name) as the GitHub `SERVER_HOST` secret for CI/CD. If you only want to
install Tailscale first, run the playbook without `vault.yml`; Ansible will
install and start the service and print a message explaining that login is
still needed.

## Provision only

```bash
ansible all -m ping -e ansible_become=false
ansible-playbook playbooks/site.yml --ask-become-pass
```

The become password is the `sudo` password for `taitran`, not the SSH key
passphrase. Run the following directly on the VM (without `ssh`):

```bash
sudo -v
sudo -n true
```

The final command should finish without output and exit successfully.
The `~/.ssh/ubuntunew` path exists on the Mac running Ansible, not inside the
VM. After checking sudo, run Ansible from the Mac in this directory.

The project configures sudo with `-S` so Ansible can provide the become
password through stdin. Use `--ask-become-pass` when running privileged
playbooks; do not use `-b=false` (that is not a valid Ansible option).

For this lab VM, passwordless sudo can be enabled with:

```bash
sudo visudo -f /etc/sudoers.d/taitran-ansible
```

```text
taitran ALL=(ALL) NOPASSWD: ALL
```

Validate it with `sudo visudo -c`. When enabled, privileged playbooks can be
run without `--ask-become-pass`.

The common, Docker, and firewall roles wait up to five minutes for Ubuntu's
automatic updates to release the APT/dpkg lock.

## Provision and deploy the application

The application deployment is intentionally a separate playbook. Set a non-empty
`app_db_password` in the encrypted `inventory/group_vars/all/vault.yml` file, and
set `app_repo_url` (or pass it with `-e`) first. On a fresh database, this value
initializes the MySQL root and application passwords. For an existing database,
it must match the current MySQL password. Keep it unchanged for later deployments.

From the `ansible` directory, edit the Vault file:

```bash
ansible-vault edit inventory/group_vars/all/vault.yml
```

Add the following variable. Choose a strong unique value for a fresh database;
for an existing database, use its current MySQL password:

```yaml
app_db_password: "YOUR_MYSQL_PASSWORD"
```

Then run the deployment playbook:

```bash
ansible-playbook playbooks/deploy.yml --ask-become-pass --ask-vault-pass \
  -e app_repo_url=https://github.com/taitran05072006/ontap.git
```

The deployment uses `/opt/ontap` on both nodes. Ansible renders
`Compose.primary.yaml` and `Compose.secondary.yaml`, then copies the host
Nginx virtual host to `/etc/nginx/sites-available/ontap` on Ubuntu 1 and
enables it through `sites-enabled`. Do not edit that file manually; edit
`ansible/roles/deploy/templates/nginx.ontap.j2` on the Mac and rerun Ansible.
The load balancer is available at `http://192.168.64.4/`. HTTPS on port 443
should be added with a real certificate (for example, Certbot or a TLS
terminating cloud load balancer) before exposing the service publicly.

## Automatic CI/CD

The workflow in `.github/workflows/ci.yml` runs backend tests and a frontend build
for pull requests. A push to `main` additionally builds multi-architecture
backend/frontend images, pushes them to Docker Hub, then runs the Ansible
deployment against both nodes with the commit SHA as the image tag.

Configure these GitHub repository secrets:

- `TS_OAUTH_CLIENT_ID`
- `TS_OAUTH_SECRET`
- `SERVER_SSH_KEY` (the private key whose public key is installed on the VM)
- `SERVER_SSH_PASSPHRASE` (only if the private key is passphrase-protected)
- `ANSIBLE_VAULT_PASSWORD`
- `APP_DB_PASSWORD`
- `DOCKERHUB_USERNAME`
- `DOCKERHUB_TOKEN`

Both VMs must be connected to the same Tailscale network, and each deployment
user must be able to run Docker without sudo. The inventory connects to
`100.107.7.37` as `taitran` and `100.104.31.2` as `trangiatai`. For local runs,
you can override `primary_host` and `secondary_host` with LAN addresses. The
`.env` file and Compose configuration created by Ansible are kept on each VM.
If a previous failed MySQL initialization left an empty/corrupt volume on a
new VM, remove only this project's volume before deploying again:

```bash
cd /opt/ontap
docker compose -f Compose.primary.yaml --env-file .env down
docker volume rm ontap_mysql-data
```

Do not run this if the database contains data you need to keep.
For production, put database credentials in an Ansible Vault file instead of
committing them to `group_vars`.

## Useful commands

```bash
ansible all -m ping -e ansible_become=false
ansible-playbook playbooks/site.yml --check --ask-become-pass
ansible-playbook playbooks/deploy.yml --tags deploy --ask-become-pass
```

Run these commands from this directory. If your shell is already inside
`ansible`, do not run `cd ansible` again.
