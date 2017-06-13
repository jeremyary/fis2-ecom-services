#! /bin/bash

cd ~
mkdir yaml-templates
cd yaml-templates
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-services/master/yaml-templates/admin-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-services/master/yaml-templates/billing-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-services/master/yaml-templates/gateway-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-services/master/yaml-templates/messaging-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-services/master/yaml-templates/mongodb-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-services/master/yaml-templates/product-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-services/master/yaml-templates/sales-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-services/master/yaml-templates/warehouse-template.yaml
