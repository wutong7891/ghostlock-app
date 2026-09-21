# Night compatibility

This branch is the `无痛临时` build of GhostLock for the Night KernelSU manager.

- Android package: `com.Night.painless`
- Required manager package: `com.Night.night`
- The manager remains KMI-free. This app carries exactly eight Night ARM64 KMI
  modules and extracts only the module matching the current Android/kernel KMI.
- After GhostLock obtains a temporary root context, it loads that external module
  with Night's `ksud`, then initializes KernelSU with
  `--package-name com.Night.night`.

Bundled KMI set:

- `android12-5.10`
- `android13-5.10`
- `android13-5.15`
- `android14-5.15`
- `android14-6.1`
- `android15-6.6`
- `android16-6.12`
- `android17-6.18`

The eight modules were produced by the Night KernelSU GitHub Actions run
`35602270787` from the Night source branch. GhostLock still requires an exact
supported kernel release/offset match; a matching KMI alone does not make an
otherwise unsupported or patched kernel exploitable.
