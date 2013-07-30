package com.celements.rights;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.security.SecurityReference;
import org.xwiki.security.SecurityReferenceFactory;
import org.xwiki.security.UserSecurityReference;
import org.xwiki.security.authorization.AccessDeniedException;
import org.xwiki.security.authorization.AuthorizationSettler;
import org.xwiki.security.authorization.DefaultAuthorizationManager;
import org.xwiki.security.authorization.Right;
import org.xwiki.security.authorization.RightDescription;
import org.xwiki.security.authorization.SecurityAccessEntry;
import org.xwiki.security.authorization.SecurityRuleEntry;
import org.xwiki.security.authorization.UnableToRegisterRightException;
import org.xwiki.security.authorization.cache.SecurityCache;
import org.xwiki.security.authorization.internal.CelementsRightServiceImpl;

@Component
@Singleton 
public class CelementsAuthorizationManager extends DefaultAuthorizationManager {

  /** Logger. **/
  @Inject
  private Logger loggerSub;
  
  /** The cached rights. */
  @Inject
  private SecurityCache securityCacheSub;
  
  /** The security reference factory. */
  @Inject
  private SecurityReferenceFactory securityReferenceFactorySub;
  
  @Inject
  @Named("celpublication")
  private AuthorizationSettler celSettler;
  
  @Override
  public void checkAccess(Right right, DocumentReference userReference,
      EntityReference entityReference) throws AccessDeniedException {
    invalidateCacheIfNecessary(userReference, entityReference);
    super.checkAccess(right, userReference, entityReference);
  }

  @Override
  public boolean hasAccess(Right right, DocumentReference userReference,
      EntityReference entityReference) {
    invalidateCacheIfNecessary(userReference, entityReference);
    return super.hasAccess(right, userReference, entityReference);
  }
  
  public void invalidateCacheIfNecessary(DocumentReference userReference, 
      EntityReference entityRef) {
    UserSecurityReference user = securityReferenceFactorySub.newUserReference(userReference);
    SecurityReference entity = securityReferenceFactorySub.newEntityReference(entityRef);
    for (SecurityReference ref = entity; ref != null; ref = ref.getParentSecurityReference()) {
      SecurityRuleEntry entry = securityCacheSub.get(ref);
      if (entry == null) {
        break;
      } else if (!entry.isEmpty() || ref.getParentSecurityReference() == null) {
        SecurityAccessEntry accessEntry = securityCacheSub.get(user, ref);
        if (accessEntry == null) {
          break;
        } else {
          if(getCelAuthSettler().isPublishActive()) {
            //XXX dirty - performance improvement: invalidate cache only if publish or
            //            unpublish date lies between now and the date the cache entry
            //            was created.
            securityCacheSub.remove(user, ref);
            loggerSub.debug("Invalidated authorization cache");
          } else {
            loggerSub.debug("Authorization cache still valid.");
          }
          break;
        }
      } 
    }
  }

  @Override
  public Right register(RightDescription rightDescription)
      throws UnableToRegisterRightException {
    return super.register(rightDescription);
  }
  
  private CelementsRightServiceImpl getCelAuthSettler() {
    return (CelementsRightServiceImpl)celSettler;
  }
}
