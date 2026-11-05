variable "aws_region" {
  description = "AWS Region trien khai"
  type        = string
  default     = "ap-southeast-2"
}

variable "availability_zone" {
  description = "Availability Zone cua EC2"
  type        = string
  default     = "ap-southeast-2a"
}

variable "pro_name" {
  description = "Ten du an"
  type        = string
  default     = "ubuntu-server-taitran"
}

variable "vpc_cidr" {
  description = "CIDR cua VPC"
  type        = string
  default     = "10.20.0.0/16"
}

variable "public_subnet_cidr" {
  description = "CIDR cua public subnet"
  type        = string
  default     = "10.20.1.0/24"
}

variable "private_subnet_cidr" {
  description = "CIDR cua private subnet"
  type        = string
  default     = "10.20.2.0/24"
}

variable "instance_type" {
  description = "Loai instance"
  type        = string
  default     = "t3.micro"
}

variable "root_volume_size" {
  description = "Dung luong root volume tinh bang GiB"
  type        = number
  default     = 20
  validation {
    condition     = var.root_volume_size > 0
    error_message = "Dung luong root volume phai lon hon 0"
  }
}

variable "admin_cidr" {
  description = "CIDR cua admin"
  type        = string
  default     = "116.105.37.221/32"
  validation {
    condition     = can(cidrhost(var.admin_cidr, 0))
    error_message = "admin_cidr phai la mot CIDR hop le"
  }
}

variable "ami_id" {
  description = "Ubuntu AMI ID in ap-southeast-2"
  type        = string
  default     = "ami-0dd60a46ecc6eac16"
}