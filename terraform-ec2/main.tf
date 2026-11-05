resource "aws_vpc" "main" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    "Name"        = "${var.pro_name}-vpc"
    "Environment" = "taitran"
    "ManagedBy"   = "Terraform"
  }
}

resource "aws_subnet" "public" {
  vpc_id                  = aws_vpc.main.id
  cidr_block              = var.public_subnet_cidr
  availability_zone       = var.availability_zone
  map_public_ip_on_launch = true
  tags = {
    Name        = "${var.pro_name}-public-subnet"
    Environment = "taitran"
    ManagedBy   = "Terraform"
  }
}

resource "aws_subnet" "private" {
  vpc_id                  = aws_vpc.main.id
  availability_zone       = var.availability_zone
  cidr_block              = var.private_subnet_cidr
  map_public_ip_on_launch = false
  tags = {
    Name        = "${var.pro_name}-private-subnet"
    Environment = "taitran"
    ManagedBy   = "Terraform"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = aws_vpc.main.id
  tags = {
    Name        = "${var.pro_name}-igw"
    Environment = "taitran"
    ManagedBy   = "Terraform"
  }
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.main.id
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.main.id
  }
  tags = {
    Name        = "${var.pro_name}-public-rt"
    Environment = "taitran"
    ManagedBy   = "Terraform"
  }
}

resource "aws_route_table" "private" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name        = "${var.pro_name}-private-rt"
    Environment = "taitran"
    ManagedBy   = "Terraform"
  }
}

resource "aws_route_table_association" "name" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}

resource "aws_route_table_association" "private" {
  subnet_id      = aws_subnet.private.id
  route_table_id = aws_route_table.private.id
}

resource "aws_security_group" "web" {
  name        = "${var.pro_name}-web-sg"
  description = "Security group for web server"
  vpc_id      = aws_vpc.main.id

  ingress {
    description = "SSH from administrator"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.admin_cidr]
  }

  ingress {
    description = "HTTP from anywhere"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS from anywhere"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name        = "${var.pro_name}-web-sg"
    Environment = "taitran"
    ManagedBy   = "Terraform"
  }
}

resource "aws_key_pair" "main" {
  key_name   = "${var.pro_name}-key"
  public_key = file(pathexpand("~/.ssh/terraform-ec2.pub"))

  tags = {
    Name        = "${var.pro_name}-key"
    Environment = "taitran"
    ManagedBy   = "Terraform"
  }
}