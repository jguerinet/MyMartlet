package ca.mcgill.mymcgill.activity.inbox;

public class Option {
	String name;
	String data;
	String path;
	
	public Option(String n,String d,String p) {
		// TODO Auto-generated constructor stub
		name = n;
		data = d;
		path = p;
		
	}

	public String getName() {
		return name;
	}

	public String getData() {
		return data;
	}

	public String getPath() {
		return path;
	}

	public boolean equals(Option o) {
		// TODO Auto-generated method stub
		if (this.name != null) return this.name.toLowerCase().equals(o.getName().toLowerCase());
		else throw new IllegalArgumentException();
	}
	


}
