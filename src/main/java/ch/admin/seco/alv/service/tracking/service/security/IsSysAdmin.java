package ch.admin.seco.alv.service.tracking.service.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(T(ch.admin.seco.alv.service.tracking.service.security.Role).ADMIN.value)")
public @interface IsSysAdmin {
}
