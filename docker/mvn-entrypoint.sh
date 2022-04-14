#! /bin/sh -eu

# Expose project dir
export PROJECT_DIR="${TEMP_VOLUME}"demo/

# Clone project
git clone https://github.com/pks9862728888/demo.git -b master "${PROJECT_DIR}"

# Run script
./"${PROJECT_DIR}"docker/script.sh
