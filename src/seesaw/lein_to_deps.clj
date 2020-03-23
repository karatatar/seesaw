(ns seesaw.lein-to-deps
  (:require [lein-to-deps.lein-to-deps :as to-deps]
            [clojure.pprint :as pprint])
  (:import [java.io FileNotFoundException]))

(defn -main []
  (let [;; Read the raw Leiningen project.clj file into a map
        {:keys [dependencies repositories]} (to-deps/read-raw "project.clj")
        ;; Read the deps.edn file into a map
        deps-map (try (read-string (slurp "deps.edn"))
                      (catch FileNotFoundException e {}))
        ;; Update the deps.edn map with the dependencies and repositories from the project.clj map
        ;; Also set the :paths entry of the deps.edn map to a hard coded value
        deps-map (merge
                  deps-map
                  {:paths ["src" "target/classes"]}
                  ;; lein-to-deps provides two helper functions to convert from the Leiningen
                  ;; dependencies and repositores formats into the tools.deps formats
                  (to-deps/format-dependencies dependencies)
                  (to-deps/format-repositories repositories))]
    ;; Pretty print the result into the deps.edn file
    (binding [*print-level* nil
              *print-length* nil]
      (spit "deps.edn" (with-out-str (pprint/pprint deps-map))))))
