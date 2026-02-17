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

resource "helm_release" "redpanda" {
  name       = "redpanda"
  repository = "https://charts.redpanda.com"
  chart      = "redpanda"
  namespace  = kubernetes_namespace.app_space.metadata[0].name

  set = [
    {
      name  = "statefulset.replicas"
      value = "1"
    },
    {
      name  = "storage.persistentVolume.enabled"
      value = "false"
    },
    {
      name  = "console.enabled"
      value = "true"
    },
    {
      name  = "tuning.tune_aio_events"
      value = "false"
    },
    # --- DEZE ZIJN NU CRUCIAAL ---
    {
      name  = "tls.enabled"
      value = "false"
    },
    {
      name  = "external.enabled"
      value = "false"
    },
    {
      name  = "config.cluster.auto_create_topics_enabled"
      value = "true"
    }
  ]
}

resource "kubernetes_manifest" "argocd_app" {
  # We voegen deze 'field_manager' toe om conflicten met handmatige acties in de UI op te lossen
  field_manager {
    force_conflicts = true
  }

  manifest = {
    apiVersion = "argoproj.io/v1alpha1"
    kind       = "Application"
    metadata = {
      name      = "reactive-processor"
      namespace = "argocd"
    }
    spec = {
      project = "default"
      source = {
        repoURL        = "https://github.com/vandenende-it/reactive-processor.git"
        targetRevision = "HEAD"
        path           = "charts/reactive-processor"
      }
      destination = {
        server    = "https://kubernetes.default.svc"
        namespace = "vandenende-reactive"
      }
      syncPolicy = {
        automated = {
          prune    = true
          selfHeal = true
        }
      }
    }
  }
  depends_on = [helm_release.argocd]
}