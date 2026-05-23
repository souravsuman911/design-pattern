# File Storage and Hierarchical Systems

## Problem Shape
Tree-like objects support create, move, delete, search, permissions, and versioning.
Examples: file system, Google Drive, Dropbox, org hierarchy.

## Core Model
- **Node**: Common parent abstraction.
- **File/Leaf**: Has content, no children.
- **Folder/Composite**: Contains children.
- **Path**: Location. Example: `/docs/a.txt`.
- **Metadata**: Name, size, owner, timestamps.

## Deep Concepts With Compact Examples
- **Composite Pattern**: Treat file and folder as `FileSystemNode`.
- **Traversal**: DFS to search or calculate folder size.
- **Path Resolution**: Split `/a/b/c.txt` and walk tree.
- **Move Safety**: Cannot move folder into its own child.
- **Permissions**: Folder permission inherited by files.
- **Versioning**: Keep old file versions after update.

## Storage Options
- **In-Memory Tree**: Best for interview implementation.
- **Parent Pointer Table**: Store `nodeId`, `parentId` in DB.
- **Full Path Index**: Fast path lookup, harder move updates.
- **Object Storage**: Store file bytes separately from metadata.
- **Search Index**: Index names/content for fast search.

## Interview Questions: Short Answers
- **Represent files/folders?** Use composite node abstraction.
- **Recursive delete?** DFS delete children first.
- **Permissions inherit?** Check direct permission, then parent chain.
- **Search?** Traverse tree or query index.
- **Version history?** Store immutable versions per file.

## Implementation Checklist
- Create base `Node`.
- Add `File` and `Folder`.
- Validate path/name conflicts.
- Implement DFS operations.
- Add permission checks.
- Separate metadata from content.
