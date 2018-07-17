package beans;

import java.util.Set;

public class Country {
	private Integer cid;
	private String cname;

	private Set<Minister> ministers;
	public Integer getCid() {
		return cid;
	}
	public void setCid(Integer cid) {
		this.cid = cid;
	}
	public String getName() {
		return cname;
	}
	public void setName(String cname) {
		this.cname = cname;
	}
	public Set<Minister> getMinisters() {
		return ministers;
	}
	public void setMinisters(Set<Minister> ministers) {
		this.ministers = ministers;
	}
	@Override
	public String toString() {
		return "Country [cid=" + cid + ", cname=" + cname + ", ministers="
				+ ministers + "]";
	}
}
