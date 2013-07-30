/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.security.authorization.internal;

import static org.xwiki.security.authorization.RuleState.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.security.GroupSecurityReference;
import org.xwiki.security.SecurityReference;
import org.xwiki.security.UserSecurityReference;
import org.xwiki.security.authorization.AuthorizationSettler;
import org.xwiki.security.authorization.Right;
import org.xwiki.security.authorization.SecurityAccess;
import org.xwiki.security.authorization.SecurityAccessEntry;
import org.xwiki.security.authorization.SecurityRuleEntry;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

@Component
@Named("celpublication")
@Singleton
public class CelementsRightServiceImpl implements AuthorizationSettler {
  @Inject
  AuthorizationSettler defaultImpl;
  
  @Inject
  Execution execution;

  @Inject
  private Logger logger;
  
  public static enum PubUnpub {
    PUBLISHED, UNPUBLISHED;
  }

  @Override
  public SecurityAccessEntry settle(UserSecurityReference user,
      Collection<GroupSecurityReference> groups, 
      Deque<SecurityRuleEntry> securityRuleEntries) {
    SecurityAccessEntry result = defaultImpl.settle(user, groups, securityRuleEntries);
    if(isPublishActive()) {
      //default behaviour: no object means published
      List<BaseObject> objs = getPublishObjects(securityRuleEntries.getFirst(
          ).getReference());
      if(isPubOverride() || (!isUnpubOverride() && !isPublished(objs))) {
        logger.info("Document not published, checking edit rights.");
        SecurityAccess access = result.getAccess();
        if(access.get(Right.EDIT) != ALLOW) {
          if(access instanceof XWikiSecurityAccess) {
            ((XWikiSecurityAccess)access).deny(Right.VIEW);
          } else {
            logger.error("Could not enforce publication dates. Access is not an " +
                "XWikiSecurityAccess");
          }
        }
      } else {
        logger.info("Document published or publication not activated.");
      }
    }
    logger.debug("Resulting rights: user=[" + result.getUserReference() + "] access=[" 
        + result.getAccess() + "] ref=[" + result.getReference() + "]");
    return result;
  }
  
  boolean isPubUnpubOverride() {
    PubUnpub val = getPubUnpubFromContext();
    return PubUnpub.PUBLISHED == val || PubUnpub.UNPUBLISHED == val;
  }
  
  boolean isPubOverride() {
    return PubUnpub.PUBLISHED == getPubUnpubFromContext();
  }
  
  boolean isUnpubOverride() {
    return PubUnpub.UNPUBLISHED == getPubUnpubFromContext();
  }
  
  PubUnpub getPubUnpubFromContext() {
    PubUnpub val = null;
    Object valObj = getContext().get("overridePubCheck");
    if((valObj != null) && (valObj instanceof PubUnpub)) {
      val = (PubUnpub)getContext().get("overridePubCheck");
    }
    return val;
  }

  List<BaseObject> getPublishObjects(SecurityReference docRef) {
    List<BaseObject> pubObjs = null;
    try {
      XWikiDocument doc = getContext().getWiki().getDocument(docRef, getContext());
      pubObjs = doc.getXObjects(getPublicationClassReference());
    } catch (XWikiException xwe) {
      logger.error("Exception while getting XWikiDocument to check publication dates", 
          xwe);
    }
    if(pubObjs == null) {
      pubObjs = Collections.emptyList();
    }
    return pubObjs;
  }

  DocumentReference getPublicationClassReference() {
    return new DocumentReference(getContext().getDatabase(), "Classes", 
        "DocumentPublication");
  }

  boolean isRestrictedRightsAction(String accessLevel) {
    return "view".equals(accessLevel) || "comment".equals(accessLevel);
  }
  
  public boolean isPublishActive() {
    DocumentReference forDocRef = null;
    if(getContext().getDoc() != null) {
      forDocRef = getContext().getDoc().getDocumentReference();
    }
    return isPublishActive(forDocRef);
  }

  public boolean isPublishActive(DocumentReference forDocRef) {
    String space = null;
    if(forDocRef != null) {
      space = forDocRef.getLastSpaceReference().getName();
    }
    String isActive = getContext().getWiki().getSpacePreference("publishdate_active", 
        space, "-1", getContext());
    if("-1".equals(isActive)) {
      isActive = getContext().getWiki().getXWikiPreference("publishdate_active", 
          "celements.publishdate.active", "0", getContext());
    }
    return "1".equals(isActive);
  }

  boolean isPublished(List<BaseObject> objs) {
    boolean isPublished = false;
    if((objs != null) && (!objs.isEmpty())) {
      for(BaseObject obj : objs) {
        if(obj != null) {
          isPublished |= isAfterStart(obj) && isBeforeEnd(obj);
        }
      }
    } else {
      isPublished = true; //no limits set mean always published
    }
    return isPublished;
  }
  
  boolean isAfterStart(BaseObject obj) {
    Calendar cal = GregorianCalendar.getInstance();
    Date pubDate = obj.getDateValue("publishDate");
    return (pubDate == null) || cal.getTime().after(pubDate);
  }

  boolean isBeforeEnd(BaseObject obj) {
    Calendar cal = GregorianCalendar.getInstance();
    Date unpubDate = obj.getDateValue("unpublishDate");
    return (unpubDate == null) || cal.getTime().before(unpubDate);
  }
  
  XWikiContext getContext() {
    return (XWikiContext)execution.getContext().getProperty("xwikicontext");
  }
}
