package me.workshop.util;

import com.google.auth.Credentials;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.secretmanager.v1.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

// Implement GCP Secret later
public class GCPPropertiesLoader {


    private static GCPPropertiesLoader instance;
    private Configuration config;

    private GCPPropertiesLoader(String secretName) {

        this.config = loadPropertiesFromSecret(secretName);
    }

    public static GCPPropertiesLoader getInstance(String propertiesFilePath) {
        if (instance == null) {
            instance = new GCPPropertiesLoader(propertiesFilePath);
        }
        return instance;
    }

    public String getProperty(String propertyName) {

        return config.getString(propertyName);
    }

    private static Configuration loadPropertiesFromSecret(String secretName) {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()){
//            Credentials credentials = GoogleCredentials.getApplicationDefault();
            GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
            String projectId = null;
            if(credentials instanceof ServiceAccountCredentials){
                ServiceAccountCredentials serviceAccountCredentials = (ServiceAccountCredentials) credentials;
                projectId =  serviceAccountCredentials.getProjectId();
            }
            if(projectId==null){
                System.out.println("Check for the poject Id or the service account GOOGLE_APPLICATION_CREDENTIALS key");
                return null;
            }


            // Get the project ID from the credentials

            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretName, "1");
            // Access the secret version
            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
            SecretPayload payload = response.getPayload();
            String secretValue = new String(payload.getData().toByteArray(), StandardCharsets.UTF_8);

            // Load properties from the secret value
            PropertiesConfiguration propertiesConfig = new PropertiesConfiguration();
            PropertiesConfigurationLayout propertiesConfigurationLayout = new PropertiesConfigurationLayout();
            propertiesConfigurationLayout.load(propertiesConfig,new StringReader(secretValue));

            return propertiesConfig;
        } catch (IOException | ConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }


}
