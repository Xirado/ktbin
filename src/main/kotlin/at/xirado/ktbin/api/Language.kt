package at.xirado.ktbin.api

/**
 * Languages used to describe the type of Gobin [File][at.xirado.ktbin.api.entity.DocumentFile]
 *
 * If you want Gobin to try to detect the language using the filename or the content automatically,
 * [Language.AUTO] can be used.
 *
 * @see language
 */
enum class Language(val id: String) {
    AUTO("auto"),
    ABAP("ABAP"),
    ABNF("ABNF"),
    ACTION_SCRIPT("ActionScript"),
    ACTION_SCRIPT_3("ActionScript 3"),
    ADA("Ada"),
    ANGULAR2("Angular2"),
    ANTLR("ANTLR"),
    APACHE_CONF("ApacheConf"),
    APL("APL"),
    APPLE_SCRIPT("AppleScript"),
    ARDUINO("Arduino"),
    AWK("Awk"),
    BALLERINA("Ballerina"),
    BASH("Bash"),
    BATCHFILE("Batchfile"),
    BIBTEX("BibTeX"),
    BICEP("Bicep"),
    BLITZ_BASIC("BlitzBasic"),
    BNF("BNF"),
    BRAINFUCK("Brainfuck"),
    BQN("BQN"),
    C("C"),
    C_SHARP("C#"),
    C_PLUS_PLUS("C++"),
    CADDYFILE("Caddyfile"),
    CADDYFILE_DIRECTIVES("Caddyfile Directives"),
    CAP_N_PROTO("Cap'n Proto"),
    CASSANDRA_CQL("Cassandra CQL"),
    CEYLON("Ceylon"),
    CFENGINE3("CFEngine3"),
    CFSTATEMENT("cfstatement"),
    CHAI_SCRIPT("ChaiScript"),
    CHAPEL("Chapel"),
    CHEETAH("Cheetah"),
    CLOJURE("Clojure"),
    CMAKE("CMake"),
    COBOL("COBOL"),
    COFFEE_SCRIPT("CoffeeScript"),
    COMMON_LISP("Common Lisp"),
    COQ("Coq"),
    CRYSTAL("Crystal"),
    CSS("CSS"),
    CYTHON("Cython"),
    D("D"),
    DART("Dart"),
    DIFF("Diff"),
    DJANGO_JINJA("Django/Jinja"),
    DOCKER("Docker"),
    DTD("DTD"),
    DYLAN("Dylan"),
    EBNF("EBNF"),
    ELIXIR("Elixir"),
    ELM("Elm"),
    EMACS_LISP("EmacsLisp"),
    ERLANG("Erlang"),
    FACTOR("Factor"),
    FISH("Fish"),
    FORTH("Forth"),
    FORTRAN("Fortran"),
    F_SHARP("FSharp"),
    GAS("GAS"),
    GD_SCRIPT("GDScript"),
    GENSHI("Genshi"),
    GENSHI_HTML("Genshi HTML"),
    GENSHI_TEXT("Genshi Text"),
    GHERKIN("Gherkin"),
    GLSL("GLSL"),
    GNUPLOT("Gnuplot"),
    GO("Go"),
    GO_HTML_TEMPLATE("Go HTML Template"),
    GO_TEXT_TEMPLATE("Go Text Template"),
    GRAPH_QL("GraphQL"),
    GROFF("Groff"),
    GROOVY("Groovy"),
    HANDLEBARS("Handlebars"),
    HASKELL("Haskell"),
    HAXE("Haxe"),
    HCL("HCL"),
    HEXDUMP("Hexdump"),
    HLB("HLB"),
    HLSL("HLSL"),
    HTML("HTML"),
    HTTP("HTTP"),
    HY("Hy"),
    IDRIS("Idris"),
    IGOR("Igor"),
    INI("INI"),
    IO("Io"),
    J("J"),
    JAVA("Java"),
    JAVA_SCRIPT("JavaScript"),
    JSON("JSON"),
    JULIA("Julia"),
    JUNGLE("Jungle"),
    KOTLIN("Kotlin"),
    LIGHTTPD_CONFIGURATION_FILE("Lighttpd configuration file"),
    LLVM("LLVM"),
    LUA("Lua"),
    MAKEFILE("Makefile"),
    MAKO("Mako"),
    MARKDOWN("markdown"),
    MASON("Mason"),
    MATHEMATICA("Mathematica"),
    MATLAB("Matlab"),
    MINI_ZINC("MiniZinc"),
    MLIR("MLIR"),
    MODULA_2("Modula-2"),
    MONKEY_C("MonkeyC"),
    MORROWIND_SCRIPT("MorrowindScript"),
    MYGHTY("Myghty"),
    MYSQL("MySQL"),
    NASM("NASM"),
    NEWSPEAK("Newspeak"),
    NGINX_CONFIGURATION_FILE("Nginx configuration file"),
    NIM("Nim"),
    NIX("Nix"),
    OBJECTIVE_C("Objective-C"),
    OCAML("OCaml"),
    OCTAVE("Octave"),
    ONES_ENTERPRISE("OnesEnterprise"),
    OPEN_EDGE_ABL("OpenEdge ABL"),
    OPEN_SCAD("OpenSCAD"),
    ORG_MODE("Org Mode"),
    PACMAN_CONF("PacmanConf"),
    PERL("Perl"),
    PHP("PHP"),
    PHTML("PHTML"),
    PIG("Pig"),
    PKGCONFIG("PkgConfig"),
    PL_PGSQL("PL/pgSQL"),
    PLAINTEXT("plaintext"),
    PONY("Pony"),
    POSTGRE_SQL_SQL_DIALECT("PostgreSQL SQL dialect"),
    POST_SCRIPT("PostScript"),
    POV_RAY("POVRay"),
    POWER_SHELL("PowerShell"),
    PROLOG("Prolog"),
    PROM_QL("PromQL"),
    PROPERTIES("Properties"),
    PROTOCOL_BUFFER("Protocol Buffer"),
    PSL("PSL"),
    PUPPET("Puppet"),
    PYTHON_2("Python 2"),
    PYTHON("Python"),
    QBASIC("QBasic"),
    R("R"),
    RACKET("Racket"),
    RAGEL("Ragel"),
    RAKU("Raku"),
    REACT("react"),
    REASON_ML("ReasonML"),
    REG("reg"),
    RE_STRUCTURED_TEXT("reStructuredText"),
    REXX("Rexx"),
    RUBY("Ruby"),
    RUST("Rust"),
    SAS("SAS"),
    SASS("Sass"),
    SCALA("Scala"),
    SCHEME("Scheme"),
    SCILAB("Scilab"),
    SCSS("SCSS"),
    SED("Sed"),
    SMALLTALK("Smalltalk"),
    SMARTY("Smarty"),
    SNOBOL("Snobol"),
    SOLIDITY("Solidity"),
    SPARQL("SPARQL"),
    SQL("SQL"),
    SQUID_CONF("SquidConf"),
    STANDARD_ML("Standard ML"),
    STAS("stas"),
    STYLUS("Stylus"),
    SVELTE("Svelte"),
    SWIFT("Swift"),
    SYSTEMD("SYSTEMD"),
    SYSTEM_VERILOG("systemverilog"),
    TABLE_GEN("TableGen"),
    TASM("TASM"),
    TCL("Tcl"),
    TCSH("Tcsh"),
    TERMCAP("Termcap"),
    TERMINFO("Terminfo"),
    TERRAFORM("Terraform"),
    TEX("TeX"),
    THRIFT("Thrift"),
    TOML("TOML"),
    TRADING_VIEW("TradingView"),
    TRANSACT_SQL("Transact-SQL"),
    TURING("Turing"),
    TURTLE("Turtle"),
    TWIG("Twig"),
    TYPE_SCRIPT("TypeScript"),
    TYPO_SCRIPT("TypoScript"),
    TYPO_SCRIPT_CSS_DATA("TypoScriptCssData"),
    TYPO_SCRIPT_HTML_DATA("TypoScriptHtmlData"),
    VB_NET("VB.net"),
    VERILOG("verilog"),
    VHDL("VHDL"),
    VHS("VHS"),
    VIM_L("VimL"),
    VUE("vue"),
    WDTE("WDTE"),
    XML("XML"),
    XORG("Xorg"),
    YAML("YAML"),
    YANG("YANG"),
    ZIG("Zig"),
}

/**
 * Tries to get a [Language] by its name.
 *
 * @param name The name of the language as used by Gobin
 *
 * @return Language corresponding to the provided [name], or `null` if it is not valid.
 */
fun language(name: String) = Language.entries.find {
    it.id.equals(name, ignoreCase = true)
}