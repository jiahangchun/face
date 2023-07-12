package org.tool;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.tool.http.FaceImageResponse;
import org.tool.http.SkipHttpsUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * @author Administrator
 */
@Slf4j
@Component
public class Digest {
    private static final int PAGE_SIZE = 10;
    public static final Integer BASE_PORT = 443;
    //智能分析盒默认名称
    public static final String MEG_NAME = "admin";
    public static final String PIC_FILE_PATH = "D:\\pic";


    /**
     * get 方法
     */
    private String getMethod(String url,
                             String ipAddress,
                             String password) {
        try {
            CloseableHttpClient httpclient = (CloseableHttpClient) new SkipHttpsUtil().wrapClient(ipAddress,
                    password);
            HttpGet getMethod = new HttpGet(url);
            HttpResponse response = httpclient.execute(getMethod);
            return EntityUtils.toString(response.getEntity(),
                    "utf-8");
        } catch (Exception e) {
            log.warn("tool.http.Digest.getMethod error 推送失败 {}",
                    e.getMessage(),
                    e);
        }
        return "";
    }


    /**
     * 获取到人脸查询的url
     */
    private String getQueryFaceUrl(int pageToken,
                                   String ip) {
        return "https://" + ip + "/v1/MEGBOX/faces" + "?pageToken=" + pageToken + "&pageSize=" + PAGE_SIZE;
    }


    /**
     * 执行方法
     */
    public void process(String ipAddress,
                        String pwd,
                        String picDownloadFilePath) {


        //人脸库查询，图片获取base64
        //将 人脸信息插入 到 旷世摄像头的人员管理中
        try {
            int pageToken = 0;
            while (true) {
                String url = this.getQueryFaceUrl(pageToken,
                        ipAddress);
                String value = this.getMethod(url,
                        ipAddress,
                        pwd);
                FaceImageResponse faceImageResponse = JSON.parseObject(value,
                        FaceImageResponse.class);
                if (faceImageResponse == null) {
                    log.warn("从智能分析盒中获取图片信息失败");
                    break;
                }
                FaceImageResponse.FaceImage data = faceImageResponse.getData();
                if (data == null) {
                    log.warn("从智能分析盒中获取图片信息失败2");
                    break;
                }
                List<FaceImageResponse.Face> face = data.getFace();
                if (face == null || face.isEmpty()) {
                    log.info(" 获取信息结束. ");
                    break;
                }
                Integer totalFaces = data.getTotalFaces();
                log.info(" 智能分析盒中一共需要同步 " + totalFaces + " 个人员信息");
                for (FaceImageResponse.Face singleFace : face) {
                    try {
                        FaceImageResponse.Face imageFace = this.fillImageInfoList(singleFace,
                                ipAddress,
                                pwd);
                        Digest.fillUserInfo(imageFace);
                        if (imageFace == null) {
                            log.warn("无法获取到 {} 的头像信息2 {}",
                                    singleFace.getName(),
                                    JSON.toJSONString(singleFace));
                            continue;
                        }
                        String dingUerId = imageFace.getDingId();
                        String name = imageFace.getName();
                        String base64Image = imageFace.getImage();
                        if (dingUerId == null || name == null || base64Image == null) {
                            log.warn("无法获取到 {} 的头像信息3 {}",
                                    name,
                                    JSON.toJSONString(singleFace));
                            continue;
                        }
                        //生成文件
                        this.createFile(picDownloadFilePath);

                        //下载文件到本地
                        String path = picDownloadFilePath + "\\" + name + "_" + dingUerId + ".jpg";
                        this.downloadImage(base64Image,
                                path);

                        log.info(" 成功下载文件 人名：{} ,文件地址：{}",
                                name,
                                path);
                    } catch (Exception e) {
                        log.error("失败，请重试 === tool.http.Digest.queryAllFaceInfo error {}",
                                e.getMessage(),
                                e);
                    }
                }
                pageToken++;
            }
        } catch (Exception e) {
            log.warn("失败，请重试 === tool.http.Digest.queryAllFaceInfo search error  {}",
                    e.getMessage(),
                    e);
        }
    }

    /**
     * 创建文件目录
     */
    private void createFile(String filePath) {
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


    /**
     * 获取图片信息
     */
    private FaceImageResponse.Face fillImageInfoList(FaceImageResponse.Face face,
                                                     String ipAddress,
                                                     String password) {
        String value = this.getMethod("https://" + ipAddress + "/v1/MEGBOX/faces/faceToken/" + face.getFaceToken(),
                ipAddress,
                password);
        FaceImageResponse.ImageInfoResponse faceImageResponse = JSON.parseObject(value,
                FaceImageResponse.ImageInfoResponse.class);
        if (faceImageResponse == null) {
            log.warn("无法获取到 {} 的头像信息 {}",
                    face.getName(),
                    JSON.toJSONString(face));
            return null;
        }
        return faceImageResponse.getData();
    }

    /**
     * 设置用户信息
     */
    private static void fillUserInfo(FaceImageResponse.Face face) {
        if (face == null) {
            return;
        }
        String desc = face.getDescription();
        if (desc == null) {
            return;
        }
        String[] infoList = desc.split("@@@");
        if (infoList.length != 2) {
            return;
        }
        if (infoList[0] != null) {
            face.setDingId(infoList[0]);
        }
        if (infoList[1] != null) {
            face.setName(infoList[1]);
        }
    }

    /**
     * 下载base64的图片
     */
    private void downloadImage(String base64,
                               String path)
            throws
            IOException {
        byte[] bs = new byte[1024];
        base64 = base64.replaceAll("data:image/jpg;base64,",
                "");
        bs = Base64.getDecoder()
                .decode(base64);
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        file = new File(path);
        try {
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
