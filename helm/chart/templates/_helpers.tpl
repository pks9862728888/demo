{{- define "chart.configmapname" -}}
{{ .Release.Name }}.configmap
{{- end -}}