//   _____                 _                  ___
//  / ____|               (_)                |__ \
// | |  __ _ __ ___   ___  _ _   _ _ __ ___     ) |
// | | |_ | '__/ _ \ / _ \| | | | | '__/ _ \   / /
// | |__| | | | (_) | (_) | | |_| | | |  __/  |_|
//  \_____|_|  \___/ \___/| |\__,_|_|  \___|  (_)
//                       _/ |
//                      |__/
//

delegate = { x, f -> f.delegate = x ; f}
extract  = { m, k, v -> m.get(k, v); m }
execute = { f -> f() }

Closure let = { Map map ->
    return delegate(
        map.inject([:]) { resultMap, entry ->
            extract(
                resultMap,
                entry.key,
                execute(
                    delegate(
                        resultMap,
                        entry.value
                    )
                )
            )
        },
        { cl -> execute(delegate(delegate, cl)) }
    )
}

(println
    (let (
        a: { "Functional Programming" },
        b: { "Groovy" })
        ({ "$a with $b" })))


