package sk.eea.arttag.rest.api;

import java.util.List;

public class PageableTagsDTO {

	private Integer position;
	private Integer totalCount;
	private Long batchId;
	private List<TagDTO> tags;
	private String resumptionToken;

	public PageableTagsDTO() {}

    public PageableTagsDTO(Integer position, Integer totalCount, Long batchId, String resumptionToken, List<TagDTO> tags) {
		super();
		this.position = position;
		this.totalCount = totalCount;
		this.batchId = batchId;
		this.tags = tags;
		this.resumptionToken = resumptionToken;
	}

	public Integer getPosition() {
		return position;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public Long getBatchId() {
		return batchId;
	}

	public List<TagDTO> getTags() {
		return tags;
	}

    public String getResumptionToken() {
        return resumptionToken;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public void setTags(List<TagDTO> tags) {
        this.tags = tags;
    }

    public void setResumptionToken(String resumptionToken) {
        this.resumptionToken = resumptionToken;
    }
}
