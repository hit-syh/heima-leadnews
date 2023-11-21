import com.heima.file.service.FileStorageService;
import com.heima.minio.MinIOApplication;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
@SpringBootTest(classes = MinIOApplication.class)
@RunWith(SpringRunner.class)
public class MinIOTest {
    public static void main(String[] args) throws Exception {
        //1.获取minio链接
        MinioClient minioClient = MinioClient.builder()
                .credentials("minioadmin", "minioadmin")
                .endpoint("http:127.0.0.1:9000").build();
        //2.上传
        FileInputStream fileInputStream=new FileInputStream("C:\\share\\黑马头条\\day2\\资料\\模板文件\\plugins\\js\\index.js");
        PutObjectArgs putObjectArgs=PutObjectArgs.builder()
                .object("plugins/js/index.js").contentType("application/javascript").bucket("leadnews").stream(fileInputStream,fileInputStream.available(),-1).build();
        minioClient.putObject(putObjectArgs);

        //
    }
    @Autowired
    FileStorageService fileStorageService;
    @Test
    public void  test() throws FileNotFoundException {
        FileInputStream fileInputStream=new FileInputStream("C:\\WorkSpaces\\IDEA\\index.html");
        String path = fileStorageService.uploadHtmlFile("", "index.html", fileInputStream);
        System.out.println(path);

    }
}
