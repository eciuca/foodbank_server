// Generated with g9.

package foodbank.it.persistence.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity(name="notifications")
public class Notification implements Serializable {

   
	private static final long serialVersionUID = -3238535903454375665L;

	/** Primary key. */
    protected static final String PK = "notificationId";

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(unique=true, nullable=false, precision=10)
    private Integer notificationId;
    @Column
    private Integer bankId;
    @Column
    private Integer orgId;
    @Column(nullable=false)
    private LocalDate creationdate;
    @Column(nullable=false, length=255)
    private String author;
    @Column(nullable=false, length=255)
    private String subject;
    @Column(nullable=false, length=255)
    private String audience;
    @Column(nullable=false, precision=5)
    private short importance;
    @Column(nullable=false, length=2)
    private String language;
    @Column(nullable=false)
    private String content;

    /** Default constructor. */
    public Notification() {
        super();
    }
    

    public Notification(Integer notificationId, Integer bankId,Integer orgId, String author, String subject, String audience,
			short importance, String language, String content) {
		super();
		this.notificationId = notificationId;
		this.bankId = bankId;
		this.orgId = orgId;
		this.creationdate =  LocalDate.now(); // do not use creationdate from DTO we need to update the time
		this.author = author;
		this.subject = subject;
		this.audience = audience;
		this.importance = importance;
		this.language = language;
		this.content = content;
	}


	/**
     * Access method for notificationId.
     *
     * @return the current value of notificationId
     */
    public Integer getNotificationId() {
        return notificationId;
    }

    /**
     * Setter method for id.
     *
     * @param aNotificationId the new value for notificationId
     */
    public void setNotificationId(Integer aNotificationId) {
        notificationId = aNotificationId;
    }

    
    public Integer getBankId() {
		return bankId;
	}


	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}


	public Integer getOrgId() {
		return orgId;
	}


	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}


	/**
     * Access method for creationdate.
     *
     * @return the current value of creationdate
     */
    public LocalDate getCreationdate() {
        return creationdate;
    }

    /**
     * Setter method for creationdate.
     *
     * @param aCreationdate the new value for creationdate
     */
    public void setCreationdate(LocalDate aCreationdate) {
        creationdate = aCreationdate;
    }

    /**
     * Access method for author.
     *
     * @return the current value of author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Setter method for author.
     *
     * @param aAuthor the new value for author
     */
    public void setAuthor(String aAuthor) {
        author = aAuthor;
    }

    /**
     * Access method for subject.
     *
     * @return the current value of subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Setter method for subject.
     *
     * @param aSubject the new value for subject
     */
    public void setSubject(String aSubject) {
        subject = aSubject;
    }

    /**
     * Access method for audience.
     *
     * @return the current value of audience
     */
    public String getAudience() {
        return audience;
    }

    /**
     * Setter method for audience.
     *
     * @param aAudience the new value for audience
     */
    public void setAudience(String aAudience) {
        audience = aAudience;
    }

    /**
     * Access method for importance.
     *
     * @return the current value of importance
     */
    public short getImportance() {
        return importance;
    }

    /**
     * Setter method for importance.
     *
     * @param aImportance the new value for importance
     */
    public void setImportance(short aImportance) {
        importance = aImportance;
    }
    
    /**
     * Access method for language.
     *
     * @return the current value of language
     */
    

    public String getLanguage() {
		return language;
	}

    /**
     * Setter method for languagee.
     *
     * @param aLanguage the new value for language
     */
	public void setLanguage(String aLanguage) {
		this.language = aLanguage;
	}

	/**
     * Access method for content.
     *
     * @return the current value of content
     */
    public String getContent() {
        return content;
    }

    /**
     * Setter method for content.
     *
     * @param aContent the new value for content
     */
    public void setContent(String aContent) {
        content = aContent;
    }

    /**
     * Compares the key for this instance with another Notifications.
     *
     * @param other The object to compare to
     * @return True if other object is instance of class Notifications and the key objects are equal
     */
    private boolean equalKeys(Object other) {
        if (this==other) {
            return true;
        }
        if (!(other instanceof Notification)) {
            return false;
        }
        Notification that = (Notification) other;
        if (this.getNotificationId() != that.getNotificationId()) {
            return false;
        }
        return true;
    }

    /**
     * Compares this instance with another Notifications.
     *
     * @param other The object to compare to
     * @return True if the objects are the same
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Notification)) return false;
        return this.equalKeys(other) && ((Notification)other).equalKeys(this);
    }

    /**
     * Returns a hash code for this instance.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        int i;
        int result = 17;
        i = getNotificationId();
        result = 37*result + i;
        return result;
    }

    /**
     * Returns a debug-friendly String representation of this instance.
     *
     * @return String representation of this instance
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("[Notification |");
        sb.append(" notificationId=").append(getNotificationId());
        sb.append("]");
        return sb.toString();
    }

    /**
     * Return all elements of the primary key.
     *
     * @return Map of key names to values
     */
    public Map<String, Object> getPrimaryKey() {
        Map<String, Object> ret = new LinkedHashMap<String, Object>(6);
        ret.put("notificationId", Integer.valueOf(getNotificationId()));
        return ret;
    }

}
