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

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.security.SecurityReference;

import com.celements.common.test.AbstractBridgedComponentTestCase;
import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xpn.xwiki.user.api.XWikiGroupService;
import com.xpn.xwiki.user.api.XWikiRightNotFoundException;

//TODO migrate remaining tests for 5.x
public class CelementsRightServiceImplTest extends AbstractBridgedComponentTestCase {
  CelementsRightServiceImpl rightService;
  XWiki xwiki;

  @Before
  public void setUp_CelementsRightServiceImplTest() throws Exception {
    rightService = new CelementsRightServiceImpl();
    xwiki = createMock(XWiki.class);
    getContext().setWiki(xwiki);
  }

  /*
  @Test
  public void testCheckRight_publishNotActive() throws XWikiRightNotFoundException, 
      XWikiException {
    XWikiGroupService gs = createMock(XWikiGroupService.class);
    expect(xwiki.getGroupService(same(getContext()))).andReturn(gs).anyTimes();
    Collection<DocumentReference> groupsList = new ArrayList<DocumentReference>();
    groupsList.add(new DocumentReference(getContext().getDatabase(), "XWiki", 
        "XWikiAllGroup"));
    expect(gs.getAllGroupsReferencesForMember(eq(new DocumentReference(getContext(
        ).getDatabase(), "XWiki", "user")), eq(0), eq(0), same(getContext()))
        ).andReturn(groupsList).anyTimes();
    Collection<DocumentReference> emptyGroupsList = Collections.emptyList();
    expect(gs.getAllGroupsReferencesForMember(eq(new DocumentReference(getContext(
        ).getDatabase(), "XWiki", "XWikiAllGroup")), eq(0), eq(0), same(getContext()))
        ).andReturn(emptyGroupsList).anyTimes();
    expect(xwiki.isVirtualMode()).andReturn(true).anyTimes();
    XWikiDocument doc = new XWikiDocument(new DocumentReference(getContext().getDatabase(
        ), "TestSpace", "TestDoc"));
    getContext().setDoc(doc);
    expect(xwiki.getSpacePreference(eq("publishdate_active"), eq("TestSpace"), 
        eq("-1"), same(getContext()))).andReturn("0").anyTimes();
    BaseObject rightObj = new BaseObject();
    rightObj.setXClassReference(new DocumentReference(getContext().getDatabase(), "XWiki",
        "XWikiRights"));
    rightObj.setStringValue("users", "XWiki.user");
    rightObj.setStringValue("levels", "view,edit");
    rightObj.setIntValue("allow", 1);
    doc.addXObject(rightObj);
    replay(gs, xwiki);
    assertTrue(rightService.checkRight("XWiki.user", doc, "view", true, true, false, 
        getContext()));
    verify(gs, xwiki);
  }

  @Test
  public void testCheckRight_publishActive_defaultNoObject(
      ) throws XWikiRightNotFoundException, XWikiException {
    XWikiGroupService gs = createMock(XWikiGroupService.class);
    expect(xwiki.getGroupService(same(getContext()))).andReturn(gs).anyTimes();
    Collection<DocumentReference> groupsList = new ArrayList<DocumentReference>();
    groupsList.add(new DocumentReference(getContext().getDatabase(), "XWiki", 
        "XWikiAllGroup"));
    expect(gs.getAllGroupsReferencesForMember(eq(new DocumentReference(getContext(
        ).getDatabase(), "XWiki", "user")), eq(0), eq(0), same(getContext()))
        ).andReturn(groupsList).anyTimes();
    Collection<DocumentReference> emptyGroupsList = Collections.emptyList();
    expect(gs.getAllGroupsReferencesForMember(eq(new DocumentReference(getContext(
        ).getDatabase(), "XWiki", "XWikiAllGroup")), eq(0), eq(0), same(getContext()))
        ).andReturn(emptyGroupsList).anyTimes();
    expect(xwiki.isVirtualMode()).andReturn(true).anyTimes();
    expect(xwiki.getSpacePreference(eq("publishdate_active"), eq("TestSpace"), eq("-1"), 
        same(getContext()))).andReturn("1").anyTimes();
    XWikiDocument doc = new XWikiDocument(new DocumentReference(getContext().getDatabase(
        ), "TestSpace", "TestDoc"));
    getContext().setDoc(doc);
    BaseObject rightObj = new BaseObject();
    rightObj.setXClassReference(new DocumentReference(getContext().getDatabase(), "XWiki",
        "XWikiRights"));
    rightObj.setStringValue("users", "XWiki.user");
    rightObj.setStringValue("levels", "view");
    rightObj.setIntValue("allow", 1);
    doc.addXObject(rightObj);
    replay(gs, xwiki);
    assertTrue(rightService.checkRight("XWiki.user", doc, "view", true, true, false, 
        getContext()));
    verify(gs, xwiki);
  }

  @Test
  public void testCheckRight_publishActive_unpublished(
      ) throws XWikiRightNotFoundException, XWikiException {
    XWikiGroupService gs = createMock(XWikiGroupService.class);
    expect(xwiki.getGroupService(same(getContext()))).andReturn(gs).anyTimes();
    Collection<DocumentReference> groupsList = new ArrayList<DocumentReference>();
    groupsList.add(new DocumentReference(getContext().getDatabase(), "XWiki", 
        "XWikiAllGroup"));
    expect(gs.getAllGroupsReferencesForMember(eq(new DocumentReference(getContext(
        ).getDatabase(), "XWiki", "user")), eq(0), eq(0), same(getContext()))
        ).andReturn(groupsList).anyTimes();
    Collection<DocumentReference> emptyGroupsList = Collections.emptyList();
    expect(gs.getAllGroupsReferencesForMember(eq(new DocumentReference(getContext(
        ).getDatabase(), "XWiki", "XWikiAllGroup")), eq(0), eq(0), same(getContext()))
        ).andReturn(emptyGroupsList).anyTimes();
    expect(xwiki.isVirtualMode()).andReturn(true).anyTimes();
    expect(xwiki.getSpacePreference(eq("publishdate_active"), eq("TestSpace"), eq("-1"), 
        same(getContext()))).andReturn("1").anyTimes();
    XWikiDocument doc = new XWikiDocument(new DocumentReference(getContext().getDatabase(
        ), "TestSpace", "TestDoc"));
    getContext().setDoc(doc);
    BaseObject rightObj = new BaseObject();
    rightObj.setXClassReference(new DocumentReference(getContext().getDatabase(), "XWiki",
        "XWikiRights"));
    rightObj.setStringValue("users", "XWiki.user");
    rightObj.setStringValue("levels", "view");
    rightObj.setIntValue("allow", 1);
    doc.addXObject(rightObj);
    Calendar gc = GregorianCalendar.getInstance();
    BaseObject obj = new BaseObject();
    gc.add(GregorianCalendar.HOUR, 1);
    obj.setDateValue("publishDate", gc.getTime());
    obj.setXClassReference(rightService.getPublicationClassReference());
    doc.addXObject(obj);
    replay(gs, xwiki);
    try {
      rightService.checkRight("XWiki.user", doc, "view", true, true, false, 
          getContext());
      fail("XWikiRightNotFoundException expected.");
    } catch(XWikiRightNotFoundException xrnfe) { }
    verify(gs, xwiki);
  }

  @Test
  public void testCheckRight_publishActive_published() throws XWikiRightNotFoundException, 
      XWikiException {
    XWikiGroupService gs = createMock(XWikiGroupService.class);
    expect(xwiki.getGroupService(same(getContext()))).andReturn(gs).anyTimes();
    Collection<DocumentReference> groupsList = new ArrayList<DocumentReference>();
    groupsList.add(new DocumentReference(getContext().getDatabase(), "XWiki", 
        "XWikiAllGroup"));
    expect(gs.getAllGroupsReferencesForMember(eq(new DocumentReference(getContext(
        ).getDatabase(), "XWiki", "user")), eq(0), eq(0), same(getContext()))
        ).andReturn(groupsList).anyTimes();
    Collection<DocumentReference> emptyGroupsList = Collections.emptyList();
    expect(gs.getAllGroupsReferencesForMember(eq(new DocumentReference(getContext(
        ).getDatabase(), "XWiki", "XWikiAllGroup")), eq(0), eq(0), same(getContext()))
        ).andReturn(emptyGroupsList).anyTimes();
    expect(xwiki.isVirtualMode()).andReturn(true).anyTimes();
    expect(xwiki.getSpacePreference(eq("publishdate_active"), eq("TestSpace"), eq("-1"), 
        same(getContext()))).andReturn("1").anyTimes();
    XWikiDocument doc = new XWikiDocument(new DocumentReference(getContext().getDatabase(
        ), "TestSpace", "TestDoc"));
    getContext().setDoc(doc);
    BaseObject rightObj = new BaseObject();
    rightObj.setXClassReference(new DocumentReference(getContext().getDatabase(), "XWiki",
        "XWikiRights"));
    rightObj.setStringValue("users", "XWiki.user");
    rightObj.setStringValue("levels", "view");
    rightObj.setIntValue("allow", 1);
    doc.addXObject(rightObj);
    Calendar gc = GregorianCalendar.getInstance();
    BaseObject obj = new BaseObject();
    gc.add(GregorianCalendar.HOUR, -1);
    obj.setDateValue("publishDate", gc.getTime());
    gc.add(GregorianCalendar.HOUR, 1);
    gc.add(GregorianCalendar.HOUR, 1);
    obj.setDateValue("unpublishDate", gc.getTime());
    obj.setXClassReference(rightService.getPublicationClassReference());
    doc.addXObject(obj);
    replay(gs, xwiki);
    assertTrue(rightService.checkRight("XWiki.user", doc, "view", true, true, false, 
        getContext()));
    verify(gs, xwiki);
  }*/

/*  @Test
  public void testGetPublishObject_null() throws XWikiException {
    DocumentReference docRef = new DocumentReference(getContext().getDatabase(), "Space", 
        "Doc");
    expect(xwiki.getDocument(same(docRef), same(getContext()))).andReturn(
        new XWikiDocument(docRef)).once();
    replay(xwiki);
    List<BaseObject> objs = rightService.getPublishObjects(docRef);
    verify(xwiki);
    assertNotNull(objs);
    assertTrue(objs.isEmpty());
  }

  @Test
  public void testGetPublishObject_hasObj() throws XWikiException {
    DocumentReference docRef = new DocumentReference(getContext().getDatabase(), "Space", 
        "Doc");
    XWikiDocument doc = new XWikiDocument(docRef);
    BaseObject obj = new BaseObject();
    obj.setXClassReference(rightService.getPublicationClassReference());
    doc.addXObject(obj);
    expect(xwiki.getDocument(same(docRef), same(getContext()))).andReturn(doc).once();
    replay(xwiki);
    assertEquals(obj, rightService.getPublishObjects(docRef).get(0));
    verify(xwiki);
  }

  @Test
  public void testGetPublishObject_hasObjs() throws XWikiException {
    DocumentReference docRef = new DocumentReference(getContext().getDatabase(), "Space", 
        "Doc");
    XWikiDocument doc = new XWikiDocument(docRef);
    BaseObject obj1 = new BaseObject();
    obj1.setXClassReference(rightService.getPublicationClassReference());
    doc.addXObject(obj1);
    BaseObject obj2 = new BaseObject();
    obj2.setXClassReference(rightService.getPublicationClassReference());
    doc.addXObject(obj2);
    expect(xwiki.getDocument(same(docRef), same(getContext()))).andReturn(doc).once();
    replay(xwiki);
    assertEquals(2, rightService.getPublishObjects(docRef).size());
    verify(xwiki);
  }*/

  @Test
  public void testIsPublished_noLimits() {
    assertTrue(rightService.isPublished(null));
    assertTrue(rightService.isPublished(new ArrayList<BaseObject>()));
  }

  @Test
  public void testIsPublished_noLimits_umpublished() {
    BaseObject obj1 = new BaseObject();
    Calendar gc = GregorianCalendar.getInstance();
    gc.add(GregorianCalendar.HOUR, 1);
    obj1.setDateValue("publishDate", gc.getTime());
    BaseObject obj2 = new BaseObject();
    gc.add(GregorianCalendar.HOUR, -2);
    obj2.setDateValue("unpublishDate", gc.getTime());
    gc.add(GregorianCalendar.HOUR, -2);
    obj2.setDateValue("publishDate", gc.getTime());
    List<BaseObject> objs = new ArrayList<BaseObject>();
    objs.add(obj1);
    objs.add(obj2);
    assertFalse(rightService.isPublished(objs));
  }

  @Test
  public void testIsPublished_noLimits_published() {
    BaseObject obj1 = new BaseObject();
    BaseObject obj3 = new BaseObject();
    Calendar gc = GregorianCalendar.getInstance();
    gc.add(GregorianCalendar.HOUR, 1);
    obj3.setDateValue("unpublishDate", gc.getTime());
    obj1.setDateValue("publishDate", gc.getTime());
    BaseObject obj2 = new BaseObject();
    gc.add(GregorianCalendar.HOUR, -2);
    obj2.setDateValue("unpublishDate", gc.getTime());
    obj3.setDateValue("publishDate", gc.getTime());
    gc.add(GregorianCalendar.HOUR, -2);
    obj2.setDateValue("publishDate", gc.getTime());
    List<BaseObject> objs = new ArrayList<BaseObject>();
    objs.add(obj1);
    objs.add(obj2);
    objs.add(obj3);
    assertTrue(rightService.isPublished(objs));
  }

/*  @Test
  public void testIsPublishActive_docNull() {
    expect(xwiki.getSpacePreference(eq("publishdate_active"), same((String)null), 
        eq("-1"), same(getContext()))).andReturn("-1").once();
    expect(xwiki.getXWikiPreference(eq("publishdate_active"), 
        eq("celements.publishdate.active"), eq("0"), same(getContext()))).andReturn("0"
        ).once();
    replay(xwiki);
    assertEquals(false, rightService.isPublishActive());
    verify(xwiki);
  }

  @Test
  public void testIsPublishActive_notSet() {
    expect(xwiki.getSpacePreference(eq("publishdate_active"), eq("TestSpace"), eq("-1"), 
        same(getContext()))).andReturn("-1").once();
    expect(xwiki.getXWikiPreference(eq("publishdate_active"), 
        eq("celements.publishdate.active"), eq("0"), same(getContext()))).andReturn("0"
        ).once();
    XWikiDocument doc = new XWikiDocument(new DocumentReference(getContext().getDatabase(
        ), "TestSpace", "TestDoc"));
    getContext().setDoc(doc);
    replay(xwiki);
    assertEquals(false, rightService.isPublishActive());
    verify(xwiki);
  }

  @Test
  public void testIsPublishActive_false() {
    expect(xwiki.getSpacePreference(eq("publishdate_active"), eq("TestSpace"), eq("-1"), 
        same(getContext()))).andReturn("0").once();
    XWikiDocument doc = new XWikiDocument(new DocumentReference(getContext().getDatabase(
        ), "TestSpace", "TestDoc"));
    getContext().setDoc(doc);
    replay(xwiki);
    assertEquals(false, rightService.isPublishActive());
    verify(xwiki);
  }

  @Test
  public void testIsPublishActive_true() {
    expect(xwiki.getSpacePreference(eq("publishdate_active"), eq("TestSpace"), eq("-1"), 
        same(getContext()))).andReturn("1").once();
    XWikiDocument doc = new XWikiDocument(new DocumentReference(getContext().getDatabase(
        ), "TestSpace", "TestDoc"));
    getContext().setDoc(doc);
    replay(xwiki);
    assertEquals(true, rightService.isPublishActive());
    verify(xwiki);
  }*/

  @Test
  public void testIsRestrictedRightsAction_view() {
    assertTrue(rightService.isRestrictedRightsAction("view"));
  }

  @Test
  public void testIsRestrictedRightsAction_comment() {
    assertTrue(rightService.isRestrictedRightsAction("comment"));
  }

  @Test
  public void testIsRestrictedRightsAction_admin() {
    assertFalse(rightService.isRestrictedRightsAction("admin"));
  }

  @Test
  public void testIsAfterStart_empty() {
    assertEquals(true, rightService.isAfterStart(new BaseObject()));
  }

  @Test
  public void testIsAfterStart_beforeStart() {
    BaseObject obj = new BaseObject();
    Calendar gc = GregorianCalendar.getInstance();
    gc.add(GregorianCalendar.HOUR, 1);
    obj.setDateValue("publishDate", gc.getTime());
    assertFalse(rightService.isAfterStart(obj));
  }

  @Test
  public void testIsAfterStart_afterStart() {
    BaseObject obj = new BaseObject();
    Calendar gc = GregorianCalendar.getInstance();
    gc.add(GregorianCalendar.HOUR, -1);
    obj.setDateValue("publishDate", gc.getTime());
    assertTrue(rightService.isAfterStart(obj));
  }

  @Test
  public void testIsBeforeEnd_empty() {
    assertEquals(true, rightService.isBeforeEnd(new BaseObject()));
  }

  @Test
  public void testIsBeforeEnd_beforeEnd() {
    BaseObject obj = new BaseObject();
    Calendar gc = GregorianCalendar.getInstance();
    gc.add(GregorianCalendar.HOUR, 1);
    obj.setDateValue("unpublishDate", gc.getTime());
    assertTrue(rightService.isBeforeEnd(obj));
  }

  @Test
  public void testIsBeforeEnd_afterEnd() {
    BaseObject obj = new BaseObject();
    Calendar gc = GregorianCalendar.getInstance();
    gc.add(GregorianCalendar.HOUR, -1);
    obj.setDateValue("unpublishDate", gc.getTime());
    assertFalse(rightService.isBeforeEnd(obj));
  }

/*  @Test
  public void testIsPubUnpubOverride_nothingSet() {
    assertFalse(rightService.isPubUnpubOverride());
  }

  @Test
  public void testIsPubUnpubOverride_wrongSet() {
    getContext().put("overridePubCheck", "test wrong type");
    assertFalse(rightService.isPubUnpubOverride());
  }

  @Test
  public void testIsPubUnpubOverride_pub() {
    getContext().put("overridePubCheck", CelementsRightServiceImpl.PubUnpub.PUBLISHED);
    assertTrue(rightService.isPubUnpubOverride());
  }

  @Test
  public void testIsPubUnpubOverride_unpub() {
    getContext().put("overridePubCheck", CelementsRightServiceImpl.PubUnpub.UNPUBLISHED);
    assertTrue(rightService.isPubUnpubOverride());
  }

  @Test
  public void testIsPubOverride_nothing() {
    assertFalse(rightService.isPubOverride());
  }

  @Test
  public void testIsPubOverride_unpub() {
    getContext().put("overridePubCheck", CelementsRightServiceImpl.PubUnpub.UNPUBLISHED);
    assertFalse(rightService.isPubOverride());
  }

  @Test
  public void testIsPubOverride_pub() {
    getContext().put("overridePubCheck", CelementsRightServiceImpl.PubUnpub.PUBLISHED);
    assertTrue(rightService.isPubOverride());
  }

  @Test
  public void testIsUnpubOverride_nothing() {
    assertFalse(rightService.isUnpubOverride());
  }

  @Test
  public void testIsUnpubOverride_unpub() {
    getContext().put("overridePubCheck", CelementsRightServiceImpl.PubUnpub.UNPUBLISHED);
    assertTrue(rightService.isUnpubOverride());
  }

  @Test
  public void testIsUnpubOverride_pub() {
    getContext().put("overridePubCheck", CelementsRightServiceImpl.PubUnpub.PUBLISHED);
    assertFalse(rightService.isUnpubOverride());
  }*/
}
