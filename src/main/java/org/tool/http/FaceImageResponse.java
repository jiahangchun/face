package org.tool.http;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FaceImageResponse
        implements Serializable {

    private Integer code;

    private String message;

    private Long timestamp;

    private Integer time_cost;

    private FaceImage data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaceImage
            implements Serializable {

        private Integer nextPageToken;

        private Integer totalFaces;

        private List<Face> face;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageInfoResponse implements Serializable{
        private Integer code;

        private String message;

        private Long timestamp;

        private Integer time_cost;

        private Face data;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Face
            implements Serializable {

        private String imageId;

        private String faceToken;

        private String description;


        /**
         * 聚合数据
         */
        private List<GroupInfo> faceGroupList;

        /**
         * 图片信息 Base64
         */
        private String image;


        private String dingId;

        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupInfo
            implements Serializable {

        private String groupName;

    }
}
