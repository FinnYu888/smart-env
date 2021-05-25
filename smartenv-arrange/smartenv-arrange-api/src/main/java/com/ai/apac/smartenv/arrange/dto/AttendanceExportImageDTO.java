package com.ai.apac.smartenv.arrange.dto;


import lombok.Data;

import java.io.InputStream;

@Data
public class AttendanceExportImageDTO {
    public InputStream image1;
    public InputStream image2;

    public String image1Detail;
    public String image2Detail;
    public String image1Title;
    public String image2Title;


}
