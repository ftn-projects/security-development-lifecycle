#!/bin/bash

# Audit script for Debian-based systems

# Colors for messages
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Viewer (batcat if available)
if command -v batcat &>/dev/null; then
    VIEWER="batcat --style=numbers,grid --paging=never"
else
    VIEWER="cat"
fi

# Args
for arg in "$@"; do
  case $arg in
    --plain) VIEWER="cat"; GREEN=''; YELLOW=''; NC='';;
  esac
done

### SYSTEM INFO ###
echo "=== SYSTEM INFO ==="
if [ -f /etc/os-release ]; then
    $VIEWER /etc/os-release
elif [ -f /etc/debian_version ]; then
    echo "Debian $(cat /etc/debian_version)" | $VIEWER
elif [ -f /etc/redhat-release ]; then
    $VIEWER /etc/redhat-release
fi
echo

### KERNEL INFO ###
echo "=== KERNEL INFO ==="
uname -a | $VIEWER
uptime -p | $VIEWER

uptime_str=$(uptime 2>/dev/null || uptime)
days=$(echo "$uptime_str" | sed -n 's/.*up \([0-9]\+\) day.*/\1/p')
if [ -n "$days" ] && [ "$days" -gt 30 ]; then
    echo -e "${YELLOW}[WARN] System uptime is ${days} days. Consider rebooting after kernel/security updates.${NC}"
else
    echo -e "${GREEN}[OK] Uptime within acceptable range.${NC}"
fi
echo

### TIME CONFIGURATION ###
echo "=== TIME CONFIGURATION ==="
timedatectl | $VIEWER
echo "Time management service:"
pgrep -af 'ntpd|chronyd|systemd-timesyncd|ntpsec' | $VIEWER

echo "NTP Peers:"
if ntpq -p -n 2>/dev/null | grep -q '[0-9]'; then
    ntpq -p -n 2>/dev/null | $VIEWER
    echo -e "${GREEN}[OK] NTP servers are reachable.${NC}"
else
    echo -e "${YELLOW}[WARN] NTP not working or unreachable peers.${NC}"
fi
echo

### INSTALLED PACKAGES ###
echo "=== MANUALLY INSTALLED PACKAGES ==="
dpkg -l $(apt-mark showmanual) | $VIEWER

### LOGGING (rsyslog) ###
echo "=== LOGGING (rsyslog) ==="
if ps -edf | grep -q [r]syslog; then
    ps -edf | grep [r]syslog | $VIEWER
    echo -e "${GREEN}[OK] rsyslog is running.${NC}"
else
    echo -e "${YELLOW}[WARN] rsyslog is NOT running.${NC}"
fi

if grep -R '^\s*@@\?\S' /etc/rsyslog.conf /etc/rsyslog.d/ 2>/dev/null; then
    grep -R '^\s*@@\?\S' /etc/rsyslog.conf /etc/rsyslog.d/ | $VIEWER
    echo -e "${GREEN}[OK] Remote logging configured.${NC}"
else
    echo -e "${YELLOW}[WARN] No remote logging configured.${NC}"
fi

# -------------------------------------------------

echo
echo "=== USER ACCOUNT REVIEW ==="
echo

echo "Users with uid 0:"
echo "-----------------------"
while IFS=: read -r username password uid gid geocos home_directory shell; do
	if [[ "$uid" -eq 0 ]]; then
		echo "$username"
	fi
done < /etc/passwd
echo

echo "User shell access:"
echo "-----------------------"
while IFS=: read -r username password uid gid geocos home_directory shell; do
	printf "%-20s %s\n" "$username" "$shell"
done < /etc/passwd
echo

echo "Hash used:"
echo "-----------------------"
USERNAME="root"
PASSWORD_HASH=$(grep "^$USERNAME:" /etc/shadow | awk -F: '{print $2}')
ALGORITHM_ID=$(echo "$PASSWORD_HASH" | awk -F'$' '{print $2}')

case "$ALGORITHM_ID" in
	1) ALGORITHM_NAME="MD5" ;;
	2a) ALGORITHM_NAME="Blowfish" ;;
	5) ALGORITHM_NAME="SHA-256" ;;
	6) ALGORITHM_NAME="SHA-512" ;;
	*) ALGORITHM_NAME="Unknown (ID: $ALGORITHM_ID)" ;;
esac
echo "$ALGORITHM_NAME" | $VIEWER
echo

echo "Sudo rules"
echo "-----------------------"
print_rules() {
	awk '!/^#|^$/ && /ALL=\(ALL/ {
		 printf "%-15s %-s\n", $1, substr($0, index($0,$2))
	}' "$1" | $VIEWER
}

print_rules /etc/sudoers

if [ -d /etc/sudoers.d ]; then
	for file in /etc/sudoers.d/*; do
		[ -f "$file" ] && print_rules "$file"
	done
fi
echo
