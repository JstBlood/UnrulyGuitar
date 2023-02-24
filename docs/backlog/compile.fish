#!/usr/bin/env fish

while inotifywait -e close_write backlog.tex
  xelatex -shell-escape -interaction=nonstopmode backlog.tex
end
