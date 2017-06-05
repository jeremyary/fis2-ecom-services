#! /bin/bash

mkdir yaml-templates
cd yaml-templates
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-servicesmaster/yaml-templates/logging-deployer.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-servicesmaster/yaml-templates/billing-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-servicesmaster/yaml-templates/gateway-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-servicesmaster/yaml-templates/messaging-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-servicesmaster/yaml-templates/presentation-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-servicesmaster/yaml-templates/product-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-servicesmaster/yaml-templates/sales-template.yaml
curl -O -L https://raw.githubusercontent.com/jeremyary/fis2-ecom-servicesmaster/yaml-templates/warehouse-template.yaml
