{{- define "chart.configmapname" -}}
{{ .Release.Name }}.configmap
{{- end -}}
{{- define "chart.secretname" -}}
{{ .Release.Name }}.secret
{{- end -}}
{{- define "chart.servicename" -}}
{{ .Release.Name }}.service
{{- end -}}
{{- define "chart.ingressname" -}}
{{ .Release.Name }}.ingress
{{- end -}}