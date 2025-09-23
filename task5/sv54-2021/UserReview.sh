#!/bin/bash

echo ""
echo "---USER ACCOUNT REVIEW---"
echo ""

echo "Users with uid 0:"
echo "-----------------------"
while IFS=: read -r username password uid gid geocos home_directory shell; do
	if [[ "$uid" -eq 0 ]]; then
		echo "$username"
	fi
done < /etc/passwd
echo ""

echo "User shell access:"
echo "-----------------------"
while IFS=: read -r username password uid gid geocos home_directory shell; do
	printf "%-20s %s\n" "$username" "$shell"
done < /etc/passwd
echo ""

echo "Hash used:"
echo "-----------------------"
USERNAME="masa"
PASSWORD_HASH=$(grep "^$USERNAME:" /etc/shadow | awk -F: '{print $2}')
ALGORITHM_ID=$(echo "$PASSWORD_HASH" | awk -F'$' '{print $2}')

case "$ALGORITHM_ID" in
	1) ALGORITHM_NAME="MD5" ;;
	2a) ALGORITHM_NAME="Blowfish" ;;
	5) ALGORITHM_NAME="SHA-256" ;;
	6) ALGORITHM_NAME="SHA-512" ;;
	*) ALGORITHM_NAME="Unknown (ID: $ALGORITHM_ID)" ;;
esac
echo "$ALGORITHM_NAME"
echo ""

echo "Sudo rules"
echo "-----------------------"
print_rules() {
	awk '!/^#|^$/ && /ALL=\(ALL/ {
		 printf "%-15s %-s\n", $1, substr($0, index($0,$2))
	}' "$1"
}

print_rules /etc/sudoers

if [ -d /etc/sudoers.d ]; then
	for file in /etc/sudoers.d/*; do
		[ -f "$file" ] && print_rules "$file"
	done
fi
echo ""


