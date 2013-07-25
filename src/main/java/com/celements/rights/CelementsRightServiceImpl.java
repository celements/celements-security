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
package com.celements.rights;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.user.api.XWikiRightNotFoundException;
import com.xpn.xwiki.user.impl.xwiki.XWikiRightServiceImpl;

public class CelementsRightServiceImpl extends XWikiRightServiceImpl {
  private static final Logger LOGGER = LoggerFactory.getLogger(
      XWikiRightServiceImpl.class);
  
  public static enum PubUnpub {
    PUBLISHED, UNPUBLISHED;
  }

  /* Adds an optional check for publish and unpublish dates to determine whether or not a 
   * page can be viewed. */
  @Override
  public boolean checkRight(String userOrGroupName, XWikiDocument doc, String accessLevel,
      boolean user, boolean allow, boolean global, XWikiContext context
      ) throws XWikiRightNotFoundException, XWikiException {
    if(isPublishActive(context) && isRestrictedRightsAction(accessLevel)) {
      //default behaviour: no object -> published
      List<BaseObject> objs = getPublishObjects(doc, context);
      if(!isPubOverride(context) && (isUnpubOverride(context) || isPublished(objs))) {
        LOGGER.info("Document published or not publish controlled.");
        return super.checkRight(userOrGroupName, doc, accessLevel, user, allow, global, 
            context);
      } else {
        LOGGER.info("Document not published, checking edit rights.");
        return super.checkRight(userOrGroupName, doc, "edit", user, allow, global, 
            context);
      }
    } else {
      if(isPubUnpubOverride(context)) {
        LOGGER.warn("Needs CelementsRightServiceImpl for publish / unpublish to work");
      }
      return super.checkRight(userOrGroupName, doc, accessLevel, user, allow, global, 
          context);
    }
  }
  
  boolean isPubUnpubOverride(XWikiContext context) {
    PubUnpub val = getPubUnpubFromContext(context);
    return PubUnpub.PUBLISHED == val || PubUnpub.UNPUBLISHED == val;
  }
  
  boolean isPubOverride(XWikiContext context) {
    return PubUnpub.PUBLISHED == getPubUnpubFromContext(context);
  }
  
  boolean isUnpubOverride(XWikiContext context) {
    return PubUnpub.UNPUBLISHED == getPubUnpubFromContext(context);
  }
  
  PubUnpub getPubUnpubFromContext(XWikiContext context) {
    PubUnpub val = null;
    Object valObj = context.get("overridePubCheck");
    if((valObj != null) && (valObj instanceof PubUnpub)) {
      val = (PubUnpub)context.get("overridePubCheck");
    }
    return val;
  }

  List<BaseObject> getPublishObjects(XWikiDocument doc, XWikiContext context) {
    return doc.getXObjects(getPublicationClassReference(context));
  }

  DocumentReference getPublicationClassReference(XWikiContext context) {
    return new DocumentReference(context.getDatabase(), "Classes", "DocumentPublication");
  }

  boolean isRestrictedRightsAction(String accessLevel) {
    return "view".equals(accessLevel) || "comment".equals(accessLevel);
  }
  
  boolean isPublishActive(XWikiContext context) {
    DocumentReference forDocRef = null;
    if(context.getDoc() != null) {
      forDocRef = context.getDoc().getDocumentReference();
    }
    return isPublishActive(forDocRef, context);
  }

  boolean isPublishActive(DocumentReference forDocRef, XWikiContext context) {
    String space = null;
    if(forDocRef != null) {
      space = forDocRef.getLastSpaceReference().getName();
    }
    String isActive = context.getWiki().getSpacePreference("publishdate_active", space, 
        "-1", context);
    if("-1".equals(isActive)) {
      isActive = context.getWiki().getXWikiPreference("publishdate_active", 
          "celements.publishdate.active", "0", context);
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
}
