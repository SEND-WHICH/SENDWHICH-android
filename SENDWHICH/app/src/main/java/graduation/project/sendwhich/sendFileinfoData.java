package graduation.project.sendwhich;

import com.google.gson.annotations.SerializedName;

public class sendFileinfoData {
    @SerializedName("sendId")
    private String sendId;

    @SerializedName("getId")
    private String getId;

    @SerializedName("filename")
    private String filename;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("filesize")
    private String filesize;

    @SerializedName("fileS3path")
    private String fileS3path;

    @SerializedName("certNum")
    private int certNum;

    @SerializedName("indexNum")
    private int indexNum;

    public sendFileinfoData(String sendId, String getId, String filename, String timestamp, String filesize, String fileS3path, int certNum) {
        this.sendId = sendId;
        this.getId = getId;
        this.filename = filename;
        this.timestamp = timestamp;
        this.filesize = filesize;
        this.fileS3path = fileS3path;
        this.certNum = certNum;
    }
}
