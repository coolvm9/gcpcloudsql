gcloud secrets create dev-props
gcloud secrets versions add dev-props --data-file=LOCAL_PATH

set this environment variable in intellij
GOOGLE_APPLICATION_CREDENTIALS = 'location of the key'

