# Access Control and Authorization

## Problem Shape
Decide whether a subject can perform an action on a resource.
Examples: RBAC, Drive permissions, admin panel, feature flags.

## Core Model
- **Subject**: User/service requesting access.
- **Resource**: Protected object. Example: document.
- **Action**: Operation. Example: `READ`, `DELETE`.
- **Role/Permission**: Grants capability.
- **Policy**: Rule that returns allow/deny.

## Deep Concepts With Compact Examples
- **RBAC**: Role grants permission. Example: `ADMIN -> DELETE_USER`.
- **ABAC**: Attributes decide. Example: user.department == resource.department.
- **ACL**: Resource-specific access. Example: document shared with user A.
- **Inheritance**: Folder permission applies to child file.
- **Deny Override**: Explicit deny beats inherited allow.
- **Audit**: Log denied sensitive actions.

## Authorization Options
- **RBAC**: Best for role-based business apps.
- **ACL**: Best for per-resource sharing.
- **ABAC**: Best for dynamic rules.
- **Policy Engine**: Best when rules are configurable.
- **Feature Flags**: Best for enabling/disabling features per user/tenant.

## Interview Questions: Short Answers
- **Model roles?** Role has many permissions; user has roles.
- **Resource-specific access?** Use ACL entries.
- **Inheritance?** Walk parent chain until permission found.
- **Deny override?** Check explicit deny before allow.
- **Cache safely?** Cache short TTL and invalidate on permission change.

## Implementation Checklist
- Define subject/action/resource.
- Keep auth separate from services.
- Add roles and permissions.
- Add ACL if sharing needed.
- Log access decisions.
- Avoid hardcoded role checks.
