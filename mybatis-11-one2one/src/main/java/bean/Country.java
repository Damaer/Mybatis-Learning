package bean;

import java.util.Set;

public class Country {
	private Integer cid;
	private String cname;
	private Minister minister;

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public Minister getMinister() {
		return minister;
	}

	public void setMinister(Minister minister) {
		this.minister = minister;
	}

	@Override
	public String toString() {
		return "Country{" +
						"cid=" + cid +
						", cname='" + cname + '\'' +
						", minister=" + minister +
						'}';
	}
}
