# Setup

First copy `safari_stale_element_test.html` file to your webserver of choice


# Running

```shell
$ mvn -Dbrowser=ff -DpageUrl=http://localhost:8080/safari_stale_element_test.html test
```

For `-Dbrowser`, you can specify `firefox`/`ff` or `chrome`/`c` to run on Firefox or Chrome, respectively. Anything else (including specifying nothing) runs on Safari.

For `-DpageUrl`, it should simply be the URL to get to the page copied above in the setup. If not supplied, the default is `http://localhost:8080/safari_stale_element_test.html`.