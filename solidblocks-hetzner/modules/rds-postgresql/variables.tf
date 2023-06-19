variable "name" {
  type        = string
  description = "unique name for the postgres rds instance"
}

variable "location" {
  type        = string
  description = "hetzner location"
}

variable "ssh_keys" {
  type        = list(number)
  description = "ssh keys for instance access"
}

variable "data_volume" {
  type        = number
  description = "data volume id"
}

variable "backup_volume" {
  type        = string
  description = "backup volume id"
  default     = 0
}

variable "solidblocks_base_url" {
  type        = string
  default     = "https://github.com"
  description = "override base url for testing purposes"
}

variable "backup_s3_bucket" {
  type        = string
  description = "AWS bucket name for S3 backups. To enable S3 backups `backup_s3_bucket`, `backup_s3_access_key` and `backup_s3_secret_key` have to be provided."
  default     = null
}

variable "backup_s3_access_key" {
  type        = string
  description = "AWS access key for S3 backups. To enable S3 backups `backup_s3_bucket`, `backup_s3_access_key` and `backup_s3_secret_key` have to be provided."
  default     = null
}

variable "backup_s3_secret_key" {
  type        = string
  description = "AWS secret key for S3 backups. To enable S3 backups `backup_s3_bucket` `backup_s3_access_key` and `backup_s3_secret_key` have to be provided."
  default     = null
}

variable "backup_full_calendar" {
  type        = string
  description = "systemd timer spec for full backups"
  default     = "*-*-* 20:00:00"
}

variable "backup_incr_calendar" {
  type        = string
  description = "systemd timer spec for incremental backups"
  default     = "*-*-* *:00:55"
}

variable "server_type" {
  type        = string
  description = "hetzner cloud server type"
  default     = "cx11"
}

variable "databases" {
  type        = list(object({ id : string, user : string, password : string }))
  description = "A list of databases to create when the instance is initialized, for example: `{ id : \"database1\", user : \"user1\", password : \"password1\" }`{"
}

variable "extra_user_data" {
  type        = string
  description = "deprecated, please use pre_script/post_script"
  default     = ""
}

variable "post_script" {
  type        = string
  description = "shell script that will be executed after the server configuration is executed"
  default     = ""
}

variable "pre_script" {
  type        = string
  description = "shell script that will be executed before the server configuration is executed"
  default     = ""
}

variable "solidblocks_cloud_init_version" {
  type        = string
  description = "used for integration tests to inject test versions"
  default     = "v0.1.12"
}

variable "solidblocks_rds_version" {
  type        = string
  description = "used for integration tests to inject test versions"
  default     = "v0.1.12"
}

variable "labels" {
  type        = map(any)
  description = "A list of labels to be attached to the server instance."
  default     = {}
}

variable "public_net_ipv4_enabled" {
  type        = bool
  description = "enable/disable public ip addresses, see also https://registry.terraform.io/providers/hetznercloud/hcloud/latest/docs/resources/server#public_net"
  default     = true
}

variable "public_net_ipv6_enabled" {
  type        = bool
  description = "enable/disable public ip addresses, see also https://registry.terraform.io/providers/hetznercloud/hcloud/latest/docs/resources/server#public_net"
  default     = true
}

variable "network_id" {
  type        = number
  description = "network the created sever should be attached to, network_ip also needs to bet set in that case"
  default     = 0
}

variable "network_ip" {
  type        = string
  description = "ip address in the attached network"
  default     = null
}
