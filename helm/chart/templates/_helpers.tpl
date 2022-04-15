{{- define "chart.configmapname" -}}
{{ .Release.Name }}.configmap
{{- end -}}
{{- define "chart.secretname" -}}
{{ .Release.Name }}.secret
{{- end -}}