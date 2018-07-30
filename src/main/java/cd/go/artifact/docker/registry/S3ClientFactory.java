package cd.go.artifact.docker.registry;
import cd.go.artifact.docker.registry.model.ArtifactStoreConfig;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3ClientFactory {
    private static final S3ClientFactory S3_CLIENT_FACTORY = new S3ClientFactory();

    public AmazonS3 s3(ArtifactStoreConfig artifactStoreConfig) throws SdkClientException {
        return createClient(artifactStoreConfig);
    }

    public static S3ClientFactory instance() {
        return S3_CLIENT_FACTORY;
    }

    private static AmazonS3 createClient(ArtifactStoreConfig artifactStoreConfig) throws SdkClientException {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(artifactStoreConfig.getAwsaccesskey(), artifactStoreConfig.getAwssecretaccesskey());
        return AmazonS3ClientBuilder.standard().withRegion(Regions.EU_WEST_1).withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
    }
}
