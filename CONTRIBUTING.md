## Contributing to Dozer
There are many ways you can help make Dozer a better piece of software - please dive in and help!
- Try surf the documentation - if somethings confusing or not clear, let us know.
- Download the code & try it out and see what you think.
- Browse the source code. Got an itch to scratch, want to tune some operation or add some feature?
- Want to do some hacking on Camel? Try surfing the our [issue tracker](https://github.com/DozerMapper/dozer/issues) for open issues or features that need to be implemented, take ownership of an issue and try fix it.
- Leave a comment on the issue to let us know you are working on it and add yourself as a watcher to get informed about all modifications.

## Getting in touch
There are various ways of communicating with the community.
- For issues or feature requests related to the code **in this repository** file a Github issue.
- For general technical questions, post a question on [Google Groups](https://groups.google.com/forum/?fromgroups=#!forum/dozer-mapper).

## Improving the documentation
- https://github.com/DozerMapper/dozer/tree/master/docs/asciidoc

## Working on the code
We recommend to work on the code from [github](https://github.com/DozerMapper/dozer/).

    git clone https://github.com/DozerMapper/dozer.git
    cd dozer

Build the project:

    mvn clean install

If you intend to work on the code and provide patches and other work you want to submit, then you can fork the project on github and work on your own fork.
The custom work you do should be done on branches you create, which can then be committed and pushed upstream, and then submitted as PRs (pull requests).
You can find many resources online how to work on github projects and how to submit work to these projects.

### Configure code style

Dozer project is using specific code style. Please steps below to import code style for your preferred IDE. 

#### IntelliJ IDEA

1. Install [CheckStyle-IDEA](https://github.com/jshiell/checkstyle-idea) plugin.
2. In `Editor -> Code Style` choose scheme where to import code style.
3. Choose action `Import Scheme -> CheckStyle Configuration` and choose file `building-tools/src/main/resouces/strict-checkstyle.xml` to import.