# SQL Script Guide

当前仓库里的 SQL 已按用途整理为 3 类：

- `init/`
  - 用于新库初始化。
  - `01_full_init.sql` 是当前最完整的建表脚本，建新库时优先执行它。
- `migrations/`
  - 用于老库增量升级。
  - 文件名前缀按当前文件最后修改日期整理，方便大致按时间顺序执行。
  - 这些脚本不少带有 `IF NOT EXISTS` / `information_schema` 判断，适合补字段、补索引、补表。
- `archive/`
  - 历史拆分脚本，仅保留参考。
  - `init_course_legacy.sql` 和 `init_resource_legacy.sql` 的内容已经被 `01_full_init.sql` 覆盖，不建议新环境单独使用。

## Recommended Usage

新环境初始化：

1. 执行 `init/01_full_init.sql`
2. 如果初始化脚本不是最新结构，再按需补跑 `migrations/` 中的脚本

已有环境升级：

1. 先备份数据库
2. 按需执行 `migrations/` 中对应模块脚本
3. 不要把 `archive/` 目录里的脚本当成正式迁移入口

## Notes

- 这些 SQL 目前存在明显的“全量初始化 + 多个历史补丁并存”的情况，所以文件数量看起来会很多。
- `update_db_alignment.sql`、`update_user_system.sql`、`update_course_system.sql`、`update_interaction_db.sql` 之间有部分职责重叠，后续如果要继续治理，建议再合并成更标准的版本化迁移。
- 部分文件里的中文注释存在编码乱码，执行通常不影响表结构，但后续值得统一转成 UTF-8。
