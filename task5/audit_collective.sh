#!/bin/bash

# Audit script for Debian-based systems
# Authors: Dimitrije Gasic, Milan Arezina, Masa Ivanov

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

echo "=== FILESYSTEM REVIEW ==="
echo

### MOUNTED PARTITIONS - /etc/fstab review ###
echo "--- Mounted Partitions (/etc/fstab) ---"
if [ -f /etc/fstab ]; then
    echo "Reviewing /etc/fstab entries:"
    echo "================================"
    
    while IFS= read -r line; do
        # Skip comments and empty lines
        if [[ ! "$line" =~ ^# ]] && [[ -n "$line" ]]; then
            device=$(echo "$line" | awk '{print $1}')
            mountpoint=$(echo "$line" | awk '{print $2}')
            fstype=$(echo "$line" | awk '{print $3}')
            options=$(echo "$line" | awk '{print $4}')
            
            echo "Device: $device"
            echo "Mountpoint: $mountpoint"
            echo "Filesystem: $fstype"
            echo "Options: $options"
            
            # Check for noatime
            if [[ "$options" == *"noatime"* ]]; then
                echo -e "${YELLOW}[WARN] noatime is enabled - prevents update of inode access time${NC}"
            else
                echo -e "${GREEN}[OK] atime enabled (access times recorded)${NC}"
            fi
            
            # Check for noexec and nosuid on specific filesystems
            if [[ "$mountpoint" == "/tmp" || "$mountpoint" == "/home" ]]; then
                if [[ "$options" != *"noexec"* ]]; then
                    echo -e "${YELLOW}[WARN] noexec not set for $mountpoint - users can execute binaries${NC}"
                else
                    echo -e "${GREEN}[OK] noexec set for $mountpoint${NC}"
                fi
                
                if [[ "$options" != *"nosuid"* ]]; then
                    echo -e "${YELLOW}[WARN] nosuid not set for $mountpoint - setuid bits are interpreted${NC}"
                else
                    echo -e "${GREEN}[OK] nosuid set for $mountpoint${NC}"
                fi
            fi
            
            # Check /dev for nosuid if using devfs
            if [[ "$mountpoint" == "/dev" && "$fstype" == "devfs" ]]; then
                if [[ "$options" != *"nosuid"* ]]; then
                    echo -e "${YELLOW}[WARN] nosuid not set for /dev (devfs)${NC}"
                else
                    echo -e "${GREEN}[OK] nosuid set for /dev${NC}"
                fi
            fi
            echo "--------------------------------"
        fi
    done < /etc/fstab
else
    echo -e "${YELLOW}[WARN] /etc/fstab not found${NC}"
fi
echo

### SENSITIVE FILES PERMISSIONS ###
echo "--- Sensitive Files Permissions ---"
echo "Checking permissions on sensitive files:"
echo "======================================"

sensitive_files=(
    "/etc/shadow"
    "/etc/gshadow"
)

for file in "${sensitive_files[@]}"; do
    if [ -e "$file" ]; then
        if [ -r "$file" ]; then
            perms=$(stat -c "%a %U:%G" "$file" 2>/dev/null)
            if [ $? -eq 0 ]; then
                # Check if file is readable by others (last digit 4-7)
                if [[ "$perms" =~ [0-9][0-9][4-7] ]]; then
                    echo -e "${RED}[CRITICAL] $file is world-readable: $perms${NC}"
                else
                    echo -e "${GREEN}[OK] $file permissions: $perms${NC}"
                fi
            fi
        else
            echo -e "${GREEN}[OK] $file not readable by current user${NC}"
        fi
    else
        echo "[INFO] $file not found"
    fi
done

# Check Apache SSL private keys
echo "Checking Apache SSL private keys:"
find /etc/apache2 /etc/ssl -name "*.key" -type f 2>/dev/null | while read keyfile; do
    if [ -r "$keyfile" ]; then
        perms=$(stat -c "%a %U:%G" "$keyfile" 2>/dev/null)
        if [[ "$perms" =~ [0-9][0-9][4-7] ]]; then
            echo -e "${RED}[CRITICAL] SSL private key world-readable: $keyfile (perms: $perms)${NC}"
        else
            echo -e "${GREEN}[OK] $keyfile permissions: $perms${NC}"
        fi
    fi
done
echo

### SETUID FILES ###
echo "--- SetUID Files ---"
echo "Finding all setuid files (excluding /proc):"
echo "=========================================="

find / -path /proc -prune -o -type f -perm -4000 -ls 2>/dev/null | $VIEWER

setuid_count=$(find / -path /proc -prune -o -type f -perm -4000 2>/dev/null | wc -l)
echo "Total setuid files found: $setuid_count"
echo

### WORLD-READABLE AND WORLD-WRITABLE FILES ###
echo "--- World-Readable and World-Writable Files ---"
echo "Finding files readable by any user:"
echo "==================================="
find / -path /proc -prune -o -type f -perm -006 -ls 2>/dev/null | head -20 | $VIEWER

echo
echo "Finding files writable by any user:"
echo "=================================="
find / -path /proc -prune -o -type f -perm -002 -ls 2>/dev/null | head -20 | $VIEWER

echo

### BACKUP FILES ###
echo "--- Backup Files Review ---"
echo "Checking for backup files and directories:"
echo "========================================="

# Check for /backup directory
if [ -d "/backup" ]; then
    echo -e "${YELLOW}[WARN] /backup directory found${NC}"
    echo "Permissions of /backup:"
    ls -la /backup 2>/dev/null | $VIEWER
    
    echo "Checking permissions of files in /backup:"
    find /backup -type f 2>/dev/null | while read file; do
        if [ -r "$file" ]; then
            perms=$(stat -c "%a" "$file" 2>/dev/null)
            if [[ "$perms" =~ [0-9][0-9][4-7] ]]; then
                echo -e "${RED}[CRITICAL] Backup file world-readable: $file (perms: $perms)${NC}"
            fi
        fi
    done
else
    echo "[INFO] /backup directory not found"
fi

# Look for common backup file patterns
echo "Searching for common backup file patterns:"
backup_patterns=("*.bak" "*.backup" "*.old" "*.orig" "*.save" "*~")
for pattern in "${backup_patterns[@]}"; do
    found_files=$(find / -path /proc -prune -o -name "$pattern" -type f 2>/dev/null | head -5)
    if [ -n "$found_files" ]; then
        echo "Found files matching $pattern:"
        echo "$found_files" | while read file; do
            if [ -r "$file" ]; then
                perms=$(stat -c "%a" "$file" 2>/dev/null)
                if [[ "$perms" =~ [0-9][0-9][4-7] ]]; then
                    echo -e "${YELLOW}[WARN] World-readable backup: $file (perms: $perms)${NC}"
                fi
            fi
        done
    fi
done

echo
echo "=== FILESYSTEM REVIEW COMPLETE ==="

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
