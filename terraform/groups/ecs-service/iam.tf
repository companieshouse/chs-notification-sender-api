resource "aws_iam_role" "notifications_upload" {
  name               = "${local.name_prefix}-upload"
  assume_role_policy = data.aws_iam_policy_document.uploader_trust.json

  tags = {
    Name               = "${var.environment}-${local.service_name}-task-role"
    Environment        = var.environment
    ECSClusterName     = "${local.name_prefix}-cluster"
    ManagedByTerraform = "true"
  }
}

resource "aws_iam_role_policy" "upload_to_s3" {
  name   = "${local.name_prefix}-upload-s3"
  role   = aws_iam_role.notifications_upload.id
  policy = data.aws_iam_policy_document.upload_to_s3.json

}
