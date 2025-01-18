import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.Scanner;

public class FileDownloader {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 提示用户输入URL
        System.out.print("请输入要下载的文件的URL (包含协议如http://或https://): ");
        String fileURL = scanner.nextLine();

        try {
            // 验证URL是否包含协议，如果没有则添加http://作为默认协议（这里简单处理，实际上可能需要更复杂的逻辑）
            if (!fileURL.startsWith("http://") && !fileURL.startsWith("https://")) {
                System.out.println("URL不包含协议，将添加http://作为默认协议。如果这是HTTPS链接，请手动输入https://。");
                fileURL = "http://" + fileURL;
            }

            // 创建URL对象
            URL url = new URL(fileURL);

            // 从URL中提取文件名（这里使用了一种简单的方法，可能不适用于所有情况）
            String fileName = Paths.get(url.getPath()).getFileName().toString();
            // 如果URL包含查询参数或片段标识符，上面的方法可能不会得到正确的文件名。更可靠的方法是解析URI。
            // 但是，为了简化示例，我们假设URL是简单的，不包含这些额外的部分。

            // 定义保存文件的路径（当前目录）
            File outputFile = new File(fileName);

            // 打开连接
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            int responseCode = httpConn.getResponseCode();

            // 总是检查HTTP响应码
            if (responseCode == HttpURLConnection.HTTP_OK) { // 成功
                // 获取文件大小
                long fileSize = httpConn.getContentLengthLong();
                long downloadedSize = 0;

                try (InputStream inputStream = httpConn.getInputStream();
                     OutputStream outputStream = new FileOutputStream(outputFile)) {

                    int bytesRead = -1;
                    byte[] buffer = new byte[4096];
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                        downloadedSize += bytesRead;
                        // 更新进度条
                        updateProgress(downloadedSize, fileSize);
                    }
                    System.out.println("\n文件已下载到: " + outputFile.getAbsolutePath());
                }
            } else {
                System.out.println("GET请求失败，响应码: " + responseCode);
            }

            httpConn.disconnect();

        } catch (MalformedURLException e) {
            System.out.println("URL格式错误: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    // 更新进度条的方法
    private static void updateProgress(long downloadedSize, long fileSize) {
        int progress = (int) ((downloadedSize * 100) / fileSize);
        System.out.print("\r下载进度: " + progress + "% [");
        for (int i = 0; i < progress / 2; i++) {
            System.out.print("=");
        }
        for (int i = 0; i < 50 - progress / 2; i++) {
            System.out.print(" ");
        }
        System.out.print("] " + downloadedSize + " / " + fileSize + " bytes");
    }
}
