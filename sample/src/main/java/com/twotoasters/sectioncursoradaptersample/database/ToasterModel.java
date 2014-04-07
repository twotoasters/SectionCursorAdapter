package com.twotoasters.sectioncursoradaptersample.database;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = ToasterModel.TABLE_NAME, id = BaseColumns._ID)
public class ToasterModel extends Model {

    @Column(name = NAME)
    public String name;

    @Column(name = IMAGE_URL)
    public String imageUrl;

    @Column(name = JOB_DESCRIPTION)
    public String jobDescription;

    @Column(name = SHORT_JOB)
    public String shortJob;

    public static final String TABLE_NAME = "toasters";
    public static final String NAME = "Name";
    public static final String IMAGE_URL = "ImageUrl";
    public static final String JOB_DESCRIPTION = "JobDescription";
    public static final String SHORT_JOB = "ShortJob";
}
