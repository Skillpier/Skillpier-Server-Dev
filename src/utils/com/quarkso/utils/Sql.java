/**
 * 
 */
package com.quarkso.utils;

/**
 * @author kingsley
 * 
 * 
 *
 */
public class Sql {

	private String select = "";
	private String from = "";
	private String except = "";

	public static Sql build() {
		return new Sql();
	}

	public Sql select(String select) {
		this.select = select;
		return this;
	}
	public Sql from(String from){
		this.from = from;
		return this;
	}

	public Sql like(String[] array, Object kw,String type) {
		if(kw == null || kw.equals("")){
			except = " (1=1) ";
			return this;
		}
		except += "(";
		for (int i = 0; i < array.length; ++i) {
			except += array[i] + " like '%" + kw + "%' ";
			if (i < array.length-1) {
				except += " "+type+" ";
			}
		}
		except += ")";
		return this;
	}
	public Sql eitherLike(String[] array, Object kw) {
		return like(array, kw, "or");
	}
	public Sql neitherLike(String[] array, Object kw) {
		return like(array, kw, "and");
	}
	public String toString(){
		return this.from + this.except;
	}
	public static void main(String[] args) {
		System.out.println(Sql.build().eitherLike(new String[]{"name","sql"}, "lily"));
	}
}
