package com.mera.varuchin.rss;

import java.net.URL;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(RssItem.class)
public abstract class RssItem_ {

	public static volatile SingularAttribute<RssItem, String> name;
	public static volatile SingularAttribute<RssItem, URL> link;
	public static volatile SingularAttribute<RssItem, String> description;
	public static volatile SingularAttribute<RssItem, Long> id;
	public static volatile SingularAttribute<RssItem, String> title;
	public static volatile SingularAttribute<RssItem, Date> pubDate;

}

