package com.xinyuan.config;

/**
 * ≥£¡ø¿‡
 * @author shxy
 *
 */
public class Constant {

	/**
	 * #!/bin/bash
	 * export ORACLE_HOME=/usr/lib/oracle/xe/app/oracle/product/10.2.0/client
	 * export ORACLE_SID=XE export PATH=$ORACLE_HOME/bin:$PATH 
	 * export NLS_LANG=AMERICAN_AMERICA.UTF8
	 * sqlplus /nolog @0153_20100609091624_301_311.sql
	 */
	/**script”Ôæ‰*/
	public static final String SCRIPT_1 = "#!/bin/bash";

	public static final String SCRIPT_2 = "export ORACLE_HOME=";

	public static final String SCRIPT_3 = "export ORACLE_SID=XE";

	public static final String SCRIPT_4 = "export PATH=$ORACLE_HOME/bin:$PATH";

	public static final String SCRIPT_5 = "export NLS_LANG=AMERICAN_AMERICA.UTF8";

	public static final String SCRIPT_6 = "sqlplus /nolog @";

    public static final String SQL_3 = "select a.fileid,a.filepath,a.filename,a.lastid,a.id,c.classname,a.companyId from downloadinfo a, "+
                            "(select max(fileid) fileid,companyid,type from downloadinfo where execultstate ='1' group by companyid,type) b, uploadtype c "+
                            "where a.companyid=b.companyid and a.lastid=b.fileid and a.type=c.id and a.execultstate='0' " +
                            "union all "+
                            "select a.fileid,a.filepath,a.filename,a.lastid,a.id,c.classname,a.companyId from downloadinfo a, uploadtype c where a.lastid='-1' and a.execultstate = '0' and a.type = c.id";

}
