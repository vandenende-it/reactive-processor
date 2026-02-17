terraform {
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.0" # Altijd goed om vast te leggen
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 3.0"
    }
  }
}

provider "kubernetes" { config_path = "~/.kube/config" }

provider "helm" {
  kubernetes = {
    config_path = pathexpand("~/.kube/config")
  }
}

# Maak een namespace voor ArgoCD
resource "kubernetes_namespace" "argocd" {
  metadata { name = "argocd" }
}

resource "helm_release" "argocd" {
  name       = "argocd"
  repository = "https://argoproj.github.io/argo-helm"
  chart      = "argo-cd"
  namespace  = kubernetes_namespace.argocd.metadata[0].name

  # Dit dwingt Terraform om te wachten tot de namespace echt bestaat
  depends_on = [kubernetes_namespace.argocd]
}

resource "kubernetes_namespace" "app_space" {
  metadata {
    name = "vandenende-reactive"
  }
}