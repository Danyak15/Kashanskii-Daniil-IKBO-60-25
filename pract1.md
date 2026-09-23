# Практическая работа №1
## Задача 1
``` bash
grep -o '^[^:]*' /etc/passwd | sort
```

## Задача 2
``` bash
grep -E '^[[:alnum:]_.-]+[[:space:]]+[0-9]+' /etc/protocols | awk '{print $2, $1}' | sort -nr | head -n 5
``` 

## Задача 3
``` bash
#!/bin/bash

text="$1"
len=${#text}

printf "+"
for ((i = 0; i < len + 2; i++))
do
    printf "-"
done

printf "+\n"
printf "| %s |\n" "$text"
printf "+"

for ((i = 0; i < len + 2; i++))
do
    printf "-"
done

printf "+\n"
```

## Задача 4
``` bash
#!/bin/bash

grep -o '[A-Za-z_][[:alnum:]_]*' "$1" | sort -u | paste -sd ' '
```

## Задача 5
``` bash
#!/bin/bash

chmod 755 "$1"
sudo cp "$1" /usr/local/bin/
```

## Задача 6
``` bash
#!/bin/bash

file="$1"
first_line=$(head -n 1 "$file")

case "$file" in
    *.c|*.js)
        if [[ "$first_line" =~ ^[[:space:]]*(//|/\*) ]]
        then
            echo "Комментарий есть"
        else
            echo "Комментария нет"
        fi
        ;;
    *.py)
        if [[ "$first_line" =~ ^[[:space:]]*# ]]
        then
            echo "Комментарий есть"
        else
            echo "Комментария нет"
        fi
        ;;
    *)
        echo "Неподдерживаемое расширение"
        ;;
esac
```

## Задача 7
``` bash
#!/bin/bash

find "$1" -type f -exec sha256sum {} + | sort | uniq -w 64 -D
```

## Задача 8
``` bash
#!/bin/bash

find "$1" -maxdepth 1 -type f -name "*.$2" -print0 | tar --null -cf archive.tar -T -
```

## Задача 9
``` bash 
#!/bin/bash

sed 's/    /\t/g' "$1" > "$2"
```

## Задача 10
``` bash
#!/bin/bash

find "$1" -maxdepth 1 -type f -empty -printf '%f\n'
```