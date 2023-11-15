package enums;

public enum DocumentType {
	PDF(".pdf"),
	JPEG(".jpeg");
	
	private String extension;
	
	public String getExtension() {
		return extension;
	}

	private DocumentType(String extension) {
		this.extension = extension;
	}

}
