package fooddiary;

import tech.ydb.auth.AuthProvider;
import tech.ydb.auth.iam.CloudAuthHelper;
import tech.ydb.core.grpc.GrpcTransport;
import tech.ydb.table.TableClient;

import java.io.IOException;
import java.time.Duration;

public class CloudConnect {

    public void launchConnect() throws IOException {
        AuthProvider authProvider = CloudAuthHelper.getServiceAccountFileAuthProvider(".keys/ydb_key.json");
        GrpcTransport transport = GrpcTransport.forConnectionString(
                        "grpcs://ydb.serverless.yandexcloud.net:2135/?database=/ru-central1/b1g8nfp5okmmg8nirubs/etngqohmoqp27djbteom"
                )
                .withAuthProvider(authProvider)
                .withSecureConnection()
                .build();
        TableClient tableClient = TableClient.newClient(transport).build();
        System.out.println(tableClient.createSession(Duration.ofSeconds(10)).join().isSuccess());
    }
}


