package example.enums;

public enum FileMetadata {
	NAME("dc:title"), ARTIST("xmpDM:artist"),
	ALBUM("xmpDM:album"), DURATION("xmpDM:duration"),
	YEAR("xmpDM:releaseDate");

	private final String identifier;

	FileMetadata(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return this.identifier;
	}
}
