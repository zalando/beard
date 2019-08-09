# 0.3.0
- Add support for scala 2.13
- Remove support for scala 2.11
- Remove monix as they don't yet have support for scala 2.13

# 0.2.0
- Add support for scala 2.12
- Remove support for scala 2.10 (use older versions of Beard if still need that)
- Add support for unless

# 0.1.2

- When a template is not found the compiler should return Failure and not throw an exception

# 0.1.1

- Use scalariform #62
- Fix chaining the filters #61

# 0.1.0

- Improve documentation
- Add support for filters
- Implement some [filters](https://danpersa.gitbooks.io/beard/content/chapter-4-filters.html)
- Fix comment grammar, change comment tag to {{- -}}
