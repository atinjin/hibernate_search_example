package prototype.hibernate.search;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Indexed
@AnalyzerDef(name = "customanalyzerLog",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
                @TokenFilterDef(factory = LowerCaseFilterFactory.class),
                @TokenFilterDef(factory = SnowballPorterFilterFactory.class, params = {
                        @org.hibernate.search.annotations.Parameter(name = "language", value = "English")
                })
        })
@Getter
@Setter
@ToString
public class Message implements Serializable {

    @Id
	private String guid;

    //Log type
	private int     logType;
    private int     logSubType;
    private int     logCategory;
    private String  logExplanation;

	private int     status;

    public enum EventContext {
        occur,
        start,
        end,
    }

    private EventContext eventContext = EventContext.occur;

    //Time
    private long    occurrenceTime;
    private long    detectionTime;
    private Date    detectionDateTime;
    private long    confirmTime;
    private Date    confirmDateTime;
    private Date    registeredDateTime;

    //Channel
    private String  sourceUid;
    @Field(index= org.hibernate.search.annotations.Index.YES, analyze= Analyze.YES, store= Store.NO)
    private String  sourceName;
    private String  sourceTypeName;
    @Field(index= org.hibernate.search.annotations.Index.YES, analyze= Analyze.YES, store= Store.NO)
    private String  sourceIpAddress;

    //Primary Channel
    private String  sourcePid;
    @Field(index= org.hibernate.search.annotations.Index.YES, analyze= Analyze.YES, store= Store.NO)
    private String  sourcePName;
    //System
    private String  sourceSystemUuid;
    private String  sourceParentSystemUuid;
    //Site or Device group
    private String  sourceGroup;
    @Field(index= org.hibernate.search.annotations.Index.YES, analyze= Analyze.YES, store= Store.NO)
    private String  sourceGroupName;

    //User
    private String  userUid;
    @Field(index= org.hibernate.search.annotations.Index.YES, analyze= Analyze.YES, store= Store.NO)
    private String  userId;
    private String  userGroupUid;
    @Field(index= org.hibernate.search.annotations.Index.YES, analyze= Analyze.YES, store= Store.NO)
    private String  userKeyword;
    @Field(index= org.hibernate.search.annotations.Index.YES, analyze= Analyze.YES, store= Store.NO)
    private String  userDescription;

    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name = "ssm_log_message_related_event")
    private List<String> relatedEvent = new ArrayList<>();

    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name = "ssm_log_message_parameter")
    private Map<String,String> parameter = new HashMap<>();

    @PostPersist
    private void postPersist() {
        this.registeredDateTime = new Date();
    }

}