package value;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.util.Map;

public class CassandraSecretManagerValue {

    public static void main(String[] args) {

        String secretNameCassandra = "projects/720089661819/secrets/cassandra-cprod-cass_ssl_rw-user/versions/1";

        try (SecretManagerServiceClient secretManagerServiceClient = SecretManagerServiceClient.create()) {
            Map<String, String> secretProperties = getStringStringMap(secretManagerServiceClient, secretNameCassandra);
            if (secretProperties != null) {
                System.out.println("Properties from secret:");
                secretProperties.forEach((key, value) -> System.out.println(key + " = " + value));
            }
        } catch (IOException e) {
            System.err.println("Error accessing secret: " + e.getMessage());
        }
    }

    private static Map<String, String> getStringStringMap(SecretManagerServiceClient secretManagerServiceClient,
                                                          String secretNameCassandra) throws JsonProcessingException {
        try {
            AccessSecretVersionResponse response = secretManagerServiceClient.accessSecretVersion(secretNameCassandra);
            ByteString secretData = response.getPayload().getData();
            String secretJson = secretData.toStringUtf8();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(secretJson, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            System.out.println("Error accessing secret: " + e.getMessage());
            return null;
        }
    }
}
