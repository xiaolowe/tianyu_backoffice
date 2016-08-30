package com.kdgz.uwifi.backoffice.bean;

/**
 * ajax请求返还结构
 * 
 * @author lanbo
 * 
 */
public class UploadData {
	
	public UploadData() {
		// default constructor
	}
	
	public UploadData(String fileId, long filesize) {
		super();
		this.fileId = fileId;
		this.filesize = filesize;
	}

	private String fileId;

	private long filesize;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}


}
