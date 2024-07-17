resource "random_string" "random" {
  length  = 5
  special = false
  upper   = false
}

resource "aws_s3_bucket" "bootstrap" {
  bucket        = "test-${random_string.random.id}"
  force_destroy = true
}

resource "google_storage_bucket" "backup" {
  project       = "solidblocks-test"
  name          = "test-${random_string.random.id}"
  location      = "EU"
  force_destroy = true
}

resource "aws_s3_bucket_public_access_block" "bootstrap" {
  bucket = aws_s3_bucket.bootstrap.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

data "aws_iam_policy_document" "bootstrap" {
  statement {
    sid     = "AllowEveryoneReadOnlyAccess"
    actions = [
      "s3:GetObject",
      "s3:ListBucket"
    ]
    principals {
      identifiers = ["*"]
      type        = "*"
    }
    resources = [
      "arn:aws:s3:::test-${random_string.random.id}",
      "arn:aws:s3:::test-${random_string.random.id}/*"
    ]
  }
}

resource "aws_s3_bucket_policy" "bootstrap" {
  bucket     = aws_s3_bucket.bootstrap.id
  policy     = data.aws_iam_policy_document.bootstrap.json
  depends_on = [
    aws_s3_bucket_ownership_controls.bootstrap, aws_s3_bucket_public_access_block.bootstrap, aws_s3_bucket_acl.bootstrap
  ]
}

resource "aws_s3_bucket_ownership_controls" "bootstrap" {
  bucket = aws_s3_bucket.bootstrap.id
  rule {
    object_ownership = "BucketOwnerPreferred"
  }
}

resource "aws_s3_bucket_acl" "bootstrap" {
  depends_on = [
    aws_s3_bucket_ownership_controls.bootstrap,
    aws_s3_bucket_public_access_block.bootstrap,
  ]

  bucket = aws_s3_bucket.bootstrap.id
  acl    = "public-read"
}

locals {
  base_path         = "${path.module}/../../../../solidblocks-cloud-init/"
  bootstrap_zip     = tolist(fileset(local.base_path, "**/solidblocks-cloud-init-${var.solidblocks_version}.zip"))[0]
  bootstrap_snippet = tolist(fileset(local.base_path, "**/solidblocks-cloud-init-bootstrap.sh"))[0]
}

resource "aws_s3_object" "bootstrap_zip" {
  bucket     = aws_s3_bucket.bootstrap.id
  key        = "pellepelster/solidblocks/releases/download/${var.solidblocks_version}/solidblocks-cloud-init-${var.solidblocks_version}.zip"
  source     = "${local.base_path}/${local.bootstrap_zip}"
  etag       = filemd5("${local.base_path}/${local.bootstrap_zip}")
  depends_on = [aws_s3_bucket_acl.bootstrap]
}

resource "aws_s3_object" "bootstrap_snippet" {
  bucket     = aws_s3_bucket.bootstrap.id
  key        = "pellepelster/solidblocks/releases/download/${var.solidblocks_version}/solidblocks-cloud-init-bootstrap.sh"
  source     = "${local.base_path}/${local.bootstrap_snippet}"
  etag       = filemd5("${local.base_path}/${local.bootstrap_snippet}")
  depends_on = [aws_s3_bucket_acl.bootstrap]
}

