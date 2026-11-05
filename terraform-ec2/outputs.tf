output "ec2_instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.main.id
}

output "ec2_public_ip" {
  description = "Public IP address of EC2"
  value       = aws_instance.main.public_ip
}

output "ec2_private_ip" {
  description = "Private IP address of EC2"
  value       = aws_instance.main.private_ip
}

output "ec2_public_dns" {
  description = "Public DNS of EC2"
  value       = aws_instance.main.public_dns
}

output "vpc_id" {
  description = "ID of the VPC"
  value       = aws_vpc.main.id
}